/**
 * Create a version of a project title suitable for URLs, package names, dir names, etc.
 */
def slugify(String str) {
    def cleanStr = str
        .toLowerCase()
        .replaceAll(/[^a-z0-9]/, '-')

    // Remove any leading or trailing dashes.
    return cleanStr.replaceAll(/^-+|-+$/, '')
}
