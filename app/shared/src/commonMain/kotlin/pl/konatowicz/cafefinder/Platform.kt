package pl.konatowicz.cafefinder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform