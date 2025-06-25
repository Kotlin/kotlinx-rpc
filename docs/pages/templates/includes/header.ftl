<#import "source_set_selector.ftl" as source_set_selector>
<#macro display>
    <header class="navigation theme-dark" id="navigation-wrapper" role="banner">
        <@template_cmd name="pathToRoot">
            <a class="library-name--link" href="${pathToRoot}index.html" tabindex="1">
                <@template_cmd name="projectName">
                    ${projectName}
                </@template_cmd>
            </a>
        </@template_cmd>
        <button class="navigation-controls--btn navigation-controls--btn_toc ui-kit_mobile-only" id="toc-toggle"
                type="button">Toggle table of contents
        </button>
        <div class="navigation-controls--break ui-kit_mobile-only"></div>
        <div class="library-version" id="library-version">
            <#-- This can be handled by the versioning plugin -->
            <@version/>
        </div>
        <div class="navigation-controls">
            <@source_set_selector.display/>
            <#if homepageLink?has_content>
                <a class="navigation-controls--btn" id="homepage-link" href="${homepageLink}"
                   style="width: fit-content; padding-left: 10px;">
                    <p style="color: var(--color-text-dt); font: var(--font-text-m); ">Back to docs</p>
                    <div class="navigation-controls--btn_homepage"
                         style="
                            height: 40px;
                            width: 40px;
                            background-color: transparent;
                            background-repeat: no-repeat;
                            background-position: 50% 50%;
                            background-size: 24px 24px;
                        ">
                    </div>
                </a>
            </#if>
            <button class="navigation-controls--btn navigation-controls--btn_theme" id="theme-toggle-button"
                    type="button">Switch theme
            </button>
            <div class="navigation-controls--btn navigation-controls--btn_search" id="searchBar" role="button">Search in
                API
            </div>
        </div>
    </header>
</#macro>
