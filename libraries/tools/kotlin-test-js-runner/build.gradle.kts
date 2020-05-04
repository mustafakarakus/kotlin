import com.moowork.gradle.node.yarn.YarnTask

description = "Simple Kotlin/JS tests runner with TeamCity reporter"

plugins {
    id("base")
    id("com.github.node-gradle.node") version "2.2.0"
}

publish()

val default = configurations.getByName(Dependency.DEFAULT_CONFIGURATION)
default.extendsFrom(configurations.publishedLibrary.get())

dependencies {
    if (!kotlinBuildProperties.isInJpsBuildIdeaSync) {
        publishedRuntime(project(":kotlin-test:kotlin-test-js"))
    }
}

node {
    version = "11.9.0"
    download = true
    nodeModulesDir = projectDir
}

tasks {
    named("yarn") {
        outputs.upToDateWhen {
            projectDir.resolve("node_modules").isDirectory
        }
        // Without it several yarns can works incorrectly
        (this as YarnTask).apply {
            args = args + "--network-concurrency" + "1" + "--mutex" + "network"
        }
    }

    register<YarnTask>("yarnBuild") {
        group = "build"

        dependsOn("yarn")
        setWorkingDir(projectDir)
        args = listOf("build")

        inputs.dir("src")
        inputs.files(
            "nodejs.ts",
            "nodejs-idle.ts",
            "karma.ts",
            "karma-kotlin-reporter.js",
            "karma-debug-runner.js",
            "karma-debug-framework.js",
            "mocha-kotlin-reporter.js",
            "tc-log-appender.js",
            "tc-log-error-webpack.js",
            "package.json",
            "rollup.config.js",
            "tsconfig.json",
            "yarn.lock"
        )
        outputs.dir("lib")
    }

    register<Delete>("cleanYarn") {
        group = "build"

        delete = setOf(
            "node_modules",
            "lib",
            ".rpt2_cache"
        )
    }

    named("clean") {
        dependsOn("cleanYarn")
    }
}

val jar by tasks.creating(Jar::class) {
    dependsOn(tasks.named("yarnBuild"))
    from(projectDir.resolve("lib"))
}

artifacts {
    add(configurations.archives.name, jar)
    add(configurations.publishedLibrary.name, jar)
}
