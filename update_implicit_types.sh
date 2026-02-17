#!/usr/bin/env bash

# Script to update implicit type definitions from official API documentation
# This script fetches the latest type definitions from Kotlin and Java documentation
# and updates the resource files used by FqNameTable for name resolution.

set -e

RESOURCES_DIR="protoc-gen/common/src/main/resources/implicit-imports"
VERSIONS_FILE="versions-root/libs.versions.toml"

# Kotlin versions to fetch and merge
# Add new versions here to support multiple Kotlin versions
# NOTE: The LATEST version in this array uses a URL without version number
KOTLIN_VERSIONS=(
    "2.0"
    "2.1"
    "2.2"
    "2.3"
)

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}Updating implicit type definitions from official documentation${NC}"

# Auto-detect all available Java SE versions
echo -n "Detecting available Java SE versions... "
JAVA_VERSIONS_RAW=$(curl -s "https://docs.oracle.com/en/java/javase/" 2>/dev/null | \
    grep -oE 'JDK [0-9]+' | \
    grep -oE '[0-9]+' | \
    sort -n -u)

if [ -z "$JAVA_VERSIONS_RAW" ]; then
    echo -e "${RED}failed${NC}"
    exit 1
else
    # Convert to array
    JAVA_VERSIONS=()
    while IFS= read -r version; do
        JAVA_VERSIONS+=("$version")
    done <<< "$JAVA_VERSIONS_RAW"
    echo -e "${GREEN}detected ${#JAVA_VERSIONS[@]} versions: ${JAVA_VERSIONS[*]}${NC}"
fi
echo ""

# Check that the latest version matches the project's Kotlin version
if [ -f "$VERSIONS_FILE" ]; then
    PROJECT_KOTLIN_VERSION=$(grep '^kotlin-lang = ' "$VERSIONS_FILE" | sed -E 's/.*"([0-9]+\.[0-9]+)\..*/\1/')
    # Get the last element of the array
    LATEST_VERSION="${KOTLIN_VERSIONS[${#KOTLIN_VERSIONS[@]}-1]}"

    echo -e "${BLUE}Project Kotlin version: $PROJECT_KOTLIN_VERSION${NC}"
    echo -e "${BLUE}Latest version in script: $LATEST_VERSION${NC}"

    if [ "$PROJECT_KOTLIN_VERSION" != "$LATEST_VERSION" ]; then
        echo -e "${RED}ERROR: Latest Kotlin version in script ($LATEST_VERSION) does not match project version ($PROJECT_KOTLIN_VERSION)${NC}"
        echo -e "${YELLOW}Please update KOTLIN_VERSIONS array in this script to include $PROJECT_KOTLIN_VERSION${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Version check passed${NC}"
    echo ""
else
    echo -e "${YELLOW}ERROR: Could not find $VERSIONS_FILE${NC}"
    exit 1
fi

# Function to extract types from Kotlin API documentation for a single version
# Usage: fetch_kotlin_types_version <version> <package_path> <output_file>
fetch_kotlin_types_version() {
    local version=$1
    local package_path=$2
    local output_file=$3

    # Latest Kotlin version uses URL without version number
    # Get the latest version from the array
    local latest_version="${KOTLIN_VERSIONS[${#KOTLIN_VERSIONS[@]}-1]}"
    local url
    if [ "$version" = "$latest_version" ]; then
        url="https://kotlinlang.org/api/core/kotlin-stdlib/${package_path}/index.html"
    else
        url="https://kotlinlang.org/api/core/${version}/kotlin-stdlib/${package_path}/index.html"
    fi

    # Download the page and extract type names from anchor-label attributes
    # The new Dokka format uses: <a ... anchor-label="TypeName" ... data-filterable-set=":kotlin-stdlib...">
    # We filter for Classlikes (not Functions, Properties, etc.)
    # Note: Using index.html explicitly to ensure we get the full HTML including deprecated classes
    # Note: Using -L to follow redirects (latest version redirects index.html to directory)
    curl -sL "$url" 2>/dev/null | \
        grep -oE 'anchor-label="[^"]+".* data-filterable-set="[^"]*"' | \
        grep '%2FClasslikes%2F' | \
        sed -E 's/anchor-label="([^"]+)".*/\1/' | \
        grep -E '^[A-Z]' | \
        LC_ALL=C sort -u > "$output_file"
}

# Function to extract types from Kotlin API documentation across multiple versions
# Usage: fetch_kotlin_types <package_path> <output_file>
fetch_kotlin_types() {
    local package_path=$1
    local output_file=$2
    local temp_dir
    temp_dir=$(mktemp -d)

    echo -e "${GREEN}Fetching types for ${package_path} across Kotlin versions: ${KOTLIN_VERSIONS[*]}${NC}"

    # Fetch types from each version
    local temp_file
    local count
    for version in "${KOTLIN_VERSIONS[@]}"; do
        temp_file="${temp_dir}/${version}.txt"
        fetch_kotlin_types_version "$version" "$package_path" "$temp_file"
        count=$(wc -l < "$temp_file" | tr -d ' ')
        echo "  Kotlin $version: $count types"
    done

    # Merge all versions and remove duplicates
    cat "${temp_dir}"/*.txt | LC_ALL=C sort -u > "$output_file"

    local total_count
    total_count=$(wc -l < "$output_file" | tr -d ' ')
    echo -e "  ${BLUE}Merged total: $total_count unique types${NC}"

    # Cleanup
    rm -rf "$temp_dir"
}

# Function to extract types from Java API documentation for a single version
# Usage: fetch_java_types_version <version> <output_file>
fetch_java_types_version() {
    local version=$1
    local output_file=$2

    # JDK 11+ uses new URL structure, JDK 7-10 use old javase/ structure
    local url
    if [ "$version" -ge 11 ]; then
        url="https://docs.oracle.com/en/java/javase/${version}/docs/api/java.base/java/lang/package-summary.html"
    else
        url="https://docs.oracle.com/javase/${version}/docs/api/java/lang/package-summary.html"
    fi

    # Download the page and extract class/interface/enum names from java.lang package
    # JDK 11+: <div class="col-first ... class-summary ..."><a ... title="... in java.lang">TypeName</a>
    # JDK 7-10: <td class="colFirst"><code>...<a ... title="class in java.lang">TypeName</a>
    # Both formats work with the same extraction pattern
    # We filter for types in the java.lang package and exclude nested classes (with dot notation)
    curl -s "$url" 2>/dev/null | \
        grep -oE 'title="[^"]*in java\.lang">[^<]+' | \
        sed 's/.*>//' | \
        grep -vE '\.' | \
        grep -E '^[A-Z]' | \
        LC_ALL=C sort -u > "$output_file"
}

# Function to extract types from Java API documentation across multiple versions
# Usage: fetch_java_types <output_file>
fetch_java_types() {
    local output_file=$1
    local temp_dir
    temp_dir=$(mktemp -d)

    echo -e "${GREEN}Fetching types for java.lang across JDK versions: ${JAVA_VERSIONS[*]}${NC}"

    # Fetch types from each version
    local temp_file
    local count
    for version in "${JAVA_VERSIONS[@]}"; do
        temp_file="${temp_dir}/${version}.txt"
        fetch_java_types_version "$version" "$temp_file"
        count=$(wc -l < "$temp_file" | tr -d ' ')
        echo "  JDK $version: $count types"
    done

    # Merge all versions and remove duplicates
    cat "${temp_dir}"/*.txt | LC_ALL=C sort -u > "$output_file"

    local total_count
    total_count=$(wc -l < "$output_file" | tr -d ' ')
    echo -e "  ${BLUE}Merged total: $total_count unique types${NC}"

    # Cleanup
    rm -rf "$temp_dir"
}

# Create resources directory if it doesn't exist
mkdir -p "$RESOURCES_DIR"

# Kotlin standard library packages
echo ""
fetch_kotlin_types "kotlin" "$RESOURCES_DIR/kotlin.txt"

echo ""
fetch_kotlin_types "kotlin.annotation" "$RESOURCES_DIR/kotlin.annotation.txt"

echo ""
fetch_kotlin_types "kotlin.collections" "$RESOURCES_DIR/kotlin.collections.txt"

echo ""
# Note: kotlin.comparisons typically contains only functions, so this may result in an empty file
fetch_kotlin_types "kotlin.comparisons" "$RESOURCES_DIR/kotlin.comparisons.txt"

echo ""
fetch_kotlin_types "kotlin.io" "$RESOURCES_DIR/kotlin.io.txt"

echo ""
fetch_kotlin_types "kotlin.ranges" "$RESOURCES_DIR/kotlin.ranges.txt"

echo ""
fetch_kotlin_types "kotlin.sequences" "$RESOURCES_DIR/kotlin.sequences.txt"

echo ""
fetch_kotlin_types "kotlin.text" "$RESOURCES_DIR/kotlin.text.txt"

echo ""
fetch_kotlin_types "kotlin.math" "$RESOURCES_DIR/kotlin.math.txt"

# JVM-specific packages
echo ""
fetch_kotlin_types "kotlin.jvm" "$RESOURCES_DIR/kotlin.jvm.txt"

# JS-specific packages
echo ""
fetch_kotlin_types "kotlin.js" "$RESOURCES_DIR/kotlin.js.txt"

# Java standard library (java.lang package)
echo ""
fetch_java_types "$RESOURCES_DIR/java.lang.txt"

echo ""
echo -e "${BLUE}All implicit type definitions updated successfully!${NC}"
echo ""
echo "Updated files in: $RESOURCES_DIR"
ls -lh "$RESOURCES_DIR"
