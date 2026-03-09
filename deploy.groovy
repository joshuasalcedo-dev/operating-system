#!/usr/bin/env groovy

def run(String cmd) {
    println "\n▶ $cmd"
    def proc = ['bash', '-c', cmd].execute()
    proc.consumeProcessOutput(System.out, System.err)
    proc.waitFor()
    if (proc.exitValue() != 0) {
        println "✗ Failed: $cmd"
        System.exit(proc.exitValue())
    }
    println "✓ Done"
}

println "=== Build ==="
run("mvn clean package -DskipTests")

println "\n=== Javadocs ==="
run("mvn javadoc:javadoc")

println "\n=== Deploy JARs → Nexus ==="
run("mvn deploy -DskipTests")

println "\n=== Deploy Javadocs → Nexus ==="
run("mvn site:deploy")

println "\n=== Deploy JARs → GitHub Packages ==="
run("mvn deploy -Pgithub-deploy -DskipTests")

println "\n=== Deploy Javadocs → GitHub Pages ==="
run("mvn site:stage scm-publish:publish-scm -Pgithub-deploy")

println "\n✅ All done!"