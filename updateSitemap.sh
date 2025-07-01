#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

# Check if the required arguments are provided
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <sitemap_file> <api_docs_dir>"
  echo "Example: $0 sitemap.xml docs/pages/api"
  exit 1
fi

sitemap_file=$1
api_docs_dir=$2

# Check if the files/directories exist
if [ ! -f "$sitemap_file" ]; then
  echo "Error: Sitemap file '$sitemap_file' does not exist."
  exit 1
fi

if [ ! -d "$api_docs_dir" ]; then
  echo "Error: API docs directory '$api_docs_dir' does not exist."
  exit 1
fi

# Get the current date in YYYY-MM-DD format
current_date=$(date +%Y-%m-%d)

# Create a temporary file to store the updated sitemap
temp_file=$(mktemp)

# Extract the first line from sitemap file (XML header), removing closing urlset tag if present
head -n 1 "$sitemap_file" | sed -En 's/<\/urlset>//' > "$temp_file"

# Find all HTML files in the API docs directory recursively
find "$api_docs_dir" -type f -name "*.html" | while read -r html_file; do
  # Convert the file path to a URL path (remove the docs/pages/ prefix)
  url_path="${html_file/#$api_docs_dir/api}"

  # Create the full URL
  url="https://kotlin.github.io/kotlinx-rpc/$url_path"

  # Add the URL entry to the temporary file
  cat >> "$temp_file" << EOF
    <url>
        <loc>$url</loc>
        <lastmod>$current_date</lastmod>
        <changefreq>monthly</changefreq>
        <priority>0.15</priority>
    </url>
EOF
done

# Add the closing tag
echo "</urlset>" >> "$temp_file"

# Replace the original sitemap file with the updated one
mv "$temp_file" "$sitemap_file"

echo "Sitemap updated successfully with entries from $api_docs_dir"
