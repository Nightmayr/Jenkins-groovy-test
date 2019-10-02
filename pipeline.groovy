import java.util.logging.Logger
import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.SimpleFormatter
import java.util.logging.LogManager
import jenkins.model.Jenkins
 
 
node ('master') {

    
    stage('test'){
        // Log into the console
        def WebAppMainLogger = LogManager.getLogManager().getLogger("hudson.WebAppMain")
        WebAppMainLogger.addHandler (new ConsoleHandler())
 
        // Log into a file
        def RunLogger = LogManager.getLogManager().getLogger("hudson.model.Run")
        def logsDir = new File(Jenkins.instance.rootDir, "logs")
        if(!logsDir.exists()){logsDir.mkdirs()}
        FileHandler handler = new FileHandler(logsDir.absolutePath+"/hudson.model.Run-%g.log", 1024 * 1024, 10, true);
        handler.setFormatter(new SimpleFormatter());
        RunLogger.addHandler(handler)

        def logger = Logger.getLogger(env.JOB_NAME) 
        
        
    }

    stage('shell test'){
        sh 'echo \\\"$$LATEST\\\"'
    
    
    }

    stage('shell test 2){
        echo 'Results included as an inline comment exactly how they are returned as of Jenkins 2.121, with $BUILD_NUMBER = 1'
        echo 'No quotes, pipeline command in single quotes'
        sh 'echo $BUILD_NUMBER' // 1
        echo 'Double quotes are silently dropped'
        sh 'echo "$BUILD_NUMBER"' // 1
        echo 'Even escaped with a single backslash they are dropped'
        sh 'echo \"$BUILD_NUMBER\"' // 1
        echo 'Using two backslashes, the quotes are preserved'
        sh 'echo \\"$BUILD_NUMBER\\"' // "1"
        echo 'Using three backslashes still results in only preserving the quotes'
        sh 'echo \\\"$BUILD_NUMBER\\\"' // "1"
        echo 'To end up with \" use \\\\\\" (yes, six backslashes)'
        sh 'echo \\\\\\"$BUILD_NUMBER\\\\\\"'
        echo 'This is fine and all, but we cannot substitute Jenkins variables in single quote strings'
        def foo = 'bar'
        sh 'echo "${foo}"' // (returns nothing)
        echo 'This does not interpolate the string but instead tries to look up "foo" on the command line, so use double quotes'
        sh "echo \"${foo}\"" // bar
        echo 'Great, more escaping is needed now. How about just concatenate the strings? Well that gets kind of ugly'
        sh 'echo \\\\\\"' + foo + '\\\\\\"' // \"bar\"
        echo 'We still needed all of that escaping and mixing concatenation is hideous!'
        echo 'There must be a better way, enter dollar slashy strings (actual term)'
        def command = $/echo \\\"${foo}\\\"/$
        sh command // \"bar\"
        echo 'String interpolation works out of the box as well as environment variables, escaped with double dollars'
        def vash = $/echo \\\"$$BUILD_NUMBER\\\" ${foo}/$
        sh vash // \"3\" bar
        echo 'It still requires escaping the escape but that is just bash being bash at that point'
        echo 'Slashy strings are the closest to raw shell input with Jenkins, although the non dollar variant seems to give an error but the dollar slash works fine'

}

