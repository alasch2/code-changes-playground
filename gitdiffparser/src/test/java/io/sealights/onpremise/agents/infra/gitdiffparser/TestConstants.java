package io.sealights.onpremise.agents.infra.gitdiffparser;

public class TestConstants {

	public static final String SIMPLE_DIFF = ""
			+ "diff --git a/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitCliCommand.java b/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitCliCommand.java\n"
			+ "index 35fabc4d..3212bf63 100644\n"
			+ "--- a/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitCliCommand.java\n"
			+ "+++ b/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitCliCommand.java\n"
			+ "@@ -62,6 +62,10 @@ public abstract class GitCliCommand<T> extends GitCommand<T> {\n"
			+ "=        }\n"
			+ "=       }\n"
			+ "=\n"
			+ "+	/**\n"
			+ "+	 * Put here \n to confuse \n parser\n"
			+ "+	 */\n"
			+ "+       public void methodS() {\n"
			+ "+\n"
			+ "+       }\n"
			+ "+\n"
			+ "=       protected boolean readGitOutputLine(BufferedReader reader) {\n"
			+ "=               try {\n"
			+ "=                       String line = reader.readLine();\n"
			;
	
	public static final String STRING_UTILS_DIFF = ""
			+ "diff --git a/java-agent-infra/src/main/java/io/sealights/onpremise/agents/infra/utils/StringUtils.java b/java-agent-infra/src/main/java/io/sealights/onpremise/agents/infra/utils/StringUtils.java\n"
			+ "index 34e0908e..e6c0dde1 100644\n"
			+ "--- a/java-agent-infra/src/main/java/io/sealights/onpremise/agents/infra/utils/StringUtils.java\n"
			+ "+++ b/java-agent-infra/src/main/java/io/sealights/onpremise/agents/infra/utils/StringUtils.java\n"
			+ "@@ -132,10 +132,13 @@ public final class StringUtils {\n"
			+ "= 		return null;\n"
			+ "= 	}\n"
			+ "=  \n" 	
			+ "+	/**\n"
			+ "+	 * Removed any \r and splits to lines by \n chars\n"
			+ "+	 */\n"
			+ "= 	public static String[] splitToLines(String original) {\n"
			+ "= 		if (original != null) {\n"
			+ "-			original.replace(END_R, EMPTY_STRING);\n"
			+ "-			String[] lines = original.split(REPLACE_END_N_PATTERN);\n"
			+ "+			String temp = original.replace(END_R, EMPTY_STRING);\n"
			+ "+			String[] lines = temp.split(REPLACE_END_N_PATTERN);\n"
			+ "= 			// remove /n from the last\n"
			+ "= 			if (lines.length > 1) {\n"
			+ "= 				lines[lines.length-1].replace(EOL, EMPTY_STRING);\n"
			;
	
	public static final String ADDED_FILE = ""
			+ "diff --git a/java-agent-events/src/main/java/io/sealights/onpremise/agentevents/engine/builders/CredentialsMaskFormatter.java b/java-agent-events/src/main/java/io/sealights/onpremise/agentevents/engine/builders/CredentialsMaskFormatter.java\n"
			+ "new file mode 100644\n"
			+ "index 00000000..83e28932\n"
			+ "--- /dev/null\n"
			+ "+++ b/java-agent-events/src/main/java/io/sealights/onpremise/agentevents/engine/builders/CredentialsMaskFormatter.java\n"
			+ "@@ -0,0 +1,46 @@\n"
			+ "+package io.sealights.onpremise.agentevents.engine.builders;\n"
			+ "+\n"
			+ "+import lombok.experimental.UtilityClass;\n"
			+ "+/**\n"
			+ "+ * Encapsulates methods for masking of credential values\n"
			+ "+ * (To be extended in the future)\n"
			+ "+ * \n"
			+ "+ * @author AlaSchneider\n"
			+ "+ *\n"
			+ "+ */\n"
			+ "+@UtilityClass\n"
			+ "+public class CredentialsMaskFormatter {\n"
			+ "+	\n"
			+ "+	public static final String MASK = \"***\";\n"
			+ "+	public static final String PASSWORD = \"password\";\n"
			+ "+	public static final String USER = \"user\";\n"
			+ "+	private static final String ASSIGN= \"=\";\n"
			+ "+    \n"
			+ "+	/**\n"
			+ "+	 * Implements simple masking of credential values in the input argv string.\n"
			+ "+	 * <p>Expects an argv as a key=value pair; replaces value to '***'.\n"
			+ "+	 * <p>Supports keys that ends with 'password' or 'user' in any lower or upper case or mixed case.\n"
			+ "+	 * \n"
			+ "+	 */\n"
			+ "+    public String maskArgValue(String originalValue) {\n"
			+ "+    	String lowerCase = originalValue.toLowerCase();\n"
			+ "+    	if (lowerCase.contains(PASSWORD)) {\n"
			+ "+    		return maskValue(originalValue);\n"
			+ "+    	}\n"
			+ "+    	\n"
			+ "+    	if (lowerCase.contains(USER)) {\n"
			+ "+    		return maskValue(originalValue);\n"
			+ "+    	}\n"
			+ "+    	\n"
			+ "+    	return originalValue;\n"
			+ "+    }\n"
			+ "+    \n"
			+ "+    private String maskValue(String originalValue) {\n"
			+ "+    	String[] keyValuePair = originalValue.split(ASSIGN);\n"
			+ "+    	if (keyValuePair.length == 2) {\n"
			+ "+    		return keyValuePair[0] + ASSIGN + MASK;\n"
			+ "+    	}\n"
			+ "+    	else return originalValue;\n"
			+ "+    }\n"
			+ "+    \n"
			+ "+}\n"
			;
	
	public static final String MULTI_HUNK_DIFF_1 = ""
			+ "diff --git a/java-agent-infra-service-proxies/src/test/java/io/sealights/onpremise/agents/infra/serviceproxy/buildsession/BuildSessionServiceProxyTest.java b/java-agent-infra-service-proxies/src/test/java/io/sealights/onpremise/agents/infra/serviceproxy/buildsession/BuildSessionServiceProxyTest.java\n"
			+ "index 4f2126bf..a003de9c 100644\n"
			+ "--- a/java-agent-infra-service-proxies/src/test/java/io/sealights/onpremise/agents/infra/serviceproxy/buildsession/BuildSessionServiceProxyTest.java\n"
			+ "+++ b/java-agent-infra-service-proxies/src/test/java/io/sealights/onpremise/agents/infra/serviceproxy/buildsession/BuildSessionServiceProxyTest.java\n"
			+ "@@ -1,10 +1,5 @@\n"
			+ "=package io.sealights.onpremise.agents.infra.serviceproxy.buildsession;\n"
			+ "=\n"
			+ "-import static org.testng.Assert.assertEquals;\n"
			+ "-import static org.testng.Assert.assertFalse;\n"
			+ "-import static org.testng.Assert.assertNotNull;\n"
			+ "-import static org.testng.Assert.assertNull;\n"
			+ "-\n"
			+ "=import java.io.File;\n"
			+ "=import java.io.IOException;\n"
			+ "=import java.nio.file.Files;\n"
			+ "@@ -33,6 +28,7 @@ import io.sealights.onpremise.agents.infra.http.api.SLHttpRequest;\n"
			+ "=import io.sealights.onpremise.agents.infra.http.api.SLHttpResult;\n"
			+ "=import io.sealights.onpremise.agents.infra.http.client.SLApacheHttpClient;\n"
			+ "=import io.sealights.onpremise.agents.infra.http.client.SLHttpClientMockUtils;\n"
			+ "+import io.sealights.onpremise.agents.infra.types.AdditionalArgumentsData;\n"
			+ "=import io.sealights.onpremise.agents.infra.types.BuildSessionData;\n"
			+ "=import io.sealights.onpremise.agents.infra.types.PullRequestParams;\n"
			+ "=\n"
			+ "@@ -40,6 +36,10 @@ public class BuildSessionServiceProxyTest extends ServiceProxyTestBase {\n"
			+ "=	\n"
			+ "=	private static final String FAKE_SESSION_ID_MDG = \"Fake session id\";\n"
			+ "=	private static final String BS_ID_FILE = \"bsid_file.txt\";\n"
			+ "+	private static final String APP = \"app\";\n"
			+ "+	private static final String BUILD = \"build\";\n"
			+ "+	private static final String BRANCH = \"branch\";\n"
			+ "+	private static final String INCLUDES = \"include*\";\n"
			+ "=	private static final String REPOSITORY_URL = \"repositoryUrl\";\n"
			+ "=	private static final int PULL_REQUEST_NUMBER = 1;\n"
			+ "=	private static final String LATEST_COMMIT = \"latestCommit\";\n"
			;
	
	public static final String MULTI_HUNK_DIFF_2 = ""
			+ "diff --git a/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitDiffsCliCommand.java b/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitDiffsCliCommand.java\n"
			+ "index 6f4ee254..6a8830f8 100644\n"
			+ "--- a/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitDiffsCliCommand.java\n"
			+ "+++ b/java-agent-git/src/main/java/io/sealights/onpremise/agents/infra/git/cli/commands/GitDiffsCliCommand.java\n"
			+ "@@ -69,2 +69 @@ public class GitDiffsCliCommand extends GitDataDiscoveryCliCommand {\n"
			+ "-    		LOG.debug(\"git-diff-cli output ({} lines): {}\", getCliOutput().size(), toStringCliOuptput());\n"
			+ "-    		getGitWorkMonitor().addDebug(String.format(\"git-diff-cli output: %s\", toStringCliOuptput()));\n"
			+ "+    		getGitWorkMonitor().addDebug(String.format(\"GIT-DIF: cli output (%s lines): %s\", getCliOutput().size(), toStringCliOuptput()));\n"
			+ "@@ -72 +71 @@ public class GitDiffsCliCommand extends GitDataDiscoveryCliCommand {\n"
			+ "-    			getGitWorkMonitor().addDebug(String.format(\"git-diff-cli result details:%s\", \n"
			+ "+    			getGitWorkMonitor().addDebug(String.format(\"git-diff handling resluts:%s\",\n" 
			;
	
	public static final String RENAMED_FILE = ""
			+ "diff --git a/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/tests/AgentCiInfoBuilderTest.java b/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentCiInfoBuilderTest.java\n"
			+ "similarity index 97%\n"
			+ "rename from java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/tests/AgentCiInfoBuilderTest.java\n"
			+ "rename to java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentCiInfoBuilderTest.java\n"
			+ "index 1e8ed9fb..700d6011 100644\n"
			+ "--- a/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/tests/AgentCiInfoBuilderTest.java\n"
			+ "+++ b/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentCiInfoBuilderTest.java\n"
			+ "@@ -1,4 +1,4 @@\n"
			+ "-package io.sealights.onpremise.agentevents.engine.builders.tests;\n"
			+ "+package io.sealights.onpremise.agentevents.engine.builders;\n"
			+ "=\n"
			+ "=import static io.sealights.onpremise.agentevents.engine.builders.CiInfoBuilder.JOB_ID_ENV;\n"
			+ "=import static io.sealights.onpremise.agentevents.engine.builders.CiInfoBuilder.JOB_NAME_ENV;\n"
			;
	
	public static final String RENAMED_FILE_MULTI_HUNK = ""
			+ "diff --git a/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/tests/AgentStartInfoBuilderTest.java b/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentStartInfoBuilderTest.java\n"
			+ "similarity index 97%\n"
			+ "rename from java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/tests/AgentStartInfoBuilderTest.java\n"
			+ "rename to java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentStartInfoBuilderTest.java\n"
			+ "index 76d67e4a..3773f7ed 100644\n"
			+ "--- a/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/tests/AgentStartInfoBuilderTest.java\n"
			+ "+++ b/java-agent-events/src/test/java/io/sealights/onpremise/agentevents/engine/builders/AgentStartInfoBuilderTest.java\n"
			+ "@@ -1,6 +1 @@\n"
			+ "-package io.sealights.onpremise.agentevents.engine.builders.tests;\n"
			+ "-\n"
			+ "-import static org.junit.Assert.assertEquals;\n"
			+ "-import static org.junit.Assert.assertFalse;\n"
			+ "-import static org.junit.Assert.assertNotNull;\n"
			+ "-import static org.junit.Assert.assertTrue;\n"
			+ "+package io.sealights.onpremise.agentevents.engine.builders;\n"
			+ "@@ -13 +8,5 @@ import org.junit.Test;\n"
			+ "-import io.sealights.onpremise.agentevents.engine.builders.AgentStartInfoBuilder;\n"
			+ "+import static org.junit.Assert.assertEquals;\n"
			+ "+import static org.junit.Assert.assertFalse;\n"
			+ "+import static org.junit.Assert.assertNotNull;\n"
			+ "+import static org.junit.Assert.assertTrue;\n"
			+ "+\n"
			;
	
}
