package stevedore;

import org.junit.Test;
import stevedore.events.DeployWasMade;
import stevedore.events.DeployWasStarted;
import stevedore.events.ReleaseWasPushed;
import stevedore.events.ReleaseWasTagged;

import static org.junit.Assert.*;

public class EnvironmentTest {
    private Environment environment;

    @Test
    public void itTagsNewReleases() {
        givenEnvironment();
        assertTrue(environment.releases().isEmpty());
        environment.tagRelease(new Version("2.5"));
        assertFalse(environment.releases().isEmpty());
        Release release = environment.getRelease("2.5");
        assertEquals(ReleaseStatus.inProgress(), release.status());
    }

    @Test
    public void itPushesReleasesToServer() {
        givenEnvironment();
        environment.tagRelease(new Version("2.5"));
        environment.release(new Version("2.5"));
        Release release = environment.getRelease("2.5");
        assertEquals(ReleaseStatus.ready(), release.status());
    }

    @Test
    public void itStartsNewDeploys() {
        givenEnvironment();
        environment.tagRelease(new Version("2.5"));
        environment.release(new Version("2.5"));
        environment.startDeploy(new Version("2.5"));
        Deploy deploy = environment.getDeploy("2.5");
        assertEquals(DeployStatus.inProgress(), deploy.status());
    }

    @Test
    public void itDeploysSpecificReleaseToServer() {
        givenEnvironment();
        environment.tagRelease(new Version("2.5"));
        environment.release(new Version("2.5"));
        environment.startDeploy(new Version("2.5"));

        Deploy deploy = environment.getDeploy("2.5");
        assertNull(environment.currentRelease());

        environment.deploy(new Version("2.5"));

        assertEquals(DeployStatus.successful(), deploy.status());
        assertEquals(deploy.release(), environment.currentRelease());
    }

    @Test
    public void itRollsbackToPreviousVersion() {
        Deploy deploy;
        givenEnvironment();
        Version version1 = new Version("1.0");
        Version version2 = new Version("2.0");
        environment.tagRelease(version1);
        environment.release(version1);
        environment.startDeploy(version1);
        environment.deploy(version1);

        environment.tagRelease(version2);
        environment.release(version2);
        environment.startDeploy(version2);
        environment.deploy(version2);

        deploy = environment.getDeploy("2.0");
        assertEquals(deploy.release(), environment.currentRelease());

        environment.startDeploy(version1);
        environment.deploy(version1);

        deploy = environment.getDeploy("1.0");
        assertEquals(deploy.release(), environment.currentRelease());
    }

    @Test
    public void itDeploysLatestRelease() {
        givenEnvironment();
        environment.tagRelease(new Version("1.0"));
        environment.release(new Version("1.0"));
        environment.tagRelease(new Version("2.0"));
        environment.release(new Version("2.0"));
        environment.tagRelease(new Version("3.0"));
        environment.release(new Version("3.0"));

        assertNull(environment.currentRelease());

        environment.startDeploy();
        environment.deploy();

        assertEquals(environment.getRelease("3.0"), environment.currentRelease());
    }

    @Test(expected = ReleaseNotFoundException.class)
    public void itFailsTryingToPushNonExistingRelease() {
        givenEnvironment();
        Version version1 = new Version("1.0");
        environment.release(version1);

        Release release = environment.getRelease(version1.version());
        assertEquals(ReleaseStatus.ready(), release.status());
    }

    @Test(expected = ReleaseNotFoundException.class)
    public void itFailsTryingToStartDeployForNonExistingRelease() {
        givenEnvironment();
        environment.startDeploy(new Version("2.5"));
        Release release = environment.getRelease("2.5");
        assertEquals(ReleaseStatus.ready(), release.status());
    }

    @Test(expected = DeployNotFoundException.class)
    public void itFailsTryingToDeployNonExistingRelease() {
        givenEnvironment();
        environment.deploy(new Version("2.5"));
        Release release = environment.getRelease("2.5");
        assertEquals(ReleaseStatus.ready(), release.status());
    }

//    @Test
//    public void itTagsNewReleases2() {
//        givenEnvironment();
//        assertTrue(environment.releases().isEmpty());
//        environment.apply(new ReleaseWasTagged("1abc", "2.5"));
//        assertFalse(environment.releases().isEmpty());
//        Release release = environment.getRelease("2.5");
//        assertEquals(ReleaseStatus.inProgress(), release.status());
//    }
//
//    @Test
//    public void itPushesReleasesToServer2() {
//        givenEnvironment();
//        environment.apply(new ReleaseWasTagged("1abc", "2.5"));
//        environment.apply(new ReleaseWasPushed("1abc", "2.5"));
//        Release release = environment.getRelease("2.5");
//        assertEquals(ReleaseStatus.ready(), release.status());
//    }
//
//    @Test
//    public void itStartsNewDeploys2() {
//        givenEnvironment();
//        environment.apply(new ReleaseWasTagged("1abc", "2.5"));
//        environment.apply(new ReleaseWasPushed("1abc", "2.5"));
//        environment.apply(new DeployWasStarted("1abc", "2.5"));
//        Deploy deploy = environment.getDeploy("2.5");
//        assertEquals(DeployStatus.inProgress(), deploy.status());
//    }
//
//    @Test
//    public void itDeploysReleasesToServer2() {
//        givenEnvironment();
//        environment.apply(new ReleaseWasTagged("1abc", "2.5"));
//        environment.apply(new ReleaseWasPushed("1abc", "2.5"));
//        environment.apply(new DeployWasStarted("1abc", "2.5"));
//
//        Deploy deploy = environment.getDeploy("2.5");
//        assertNull(environment.currentRelease());
//
//        environment.apply(new DeployWasMade("1abc", "2.5"));
//
//        assertEquals(DeployStatus.successful(), deploy.status());
//        assertEquals(deploy.release(), environment.currentRelease());
//    }
//
//    @Test
//    public void itRollsbackToPreviousVersion2() {
//        Deploy deploy;
//        givenEnvironment();
//        environment.apply(new ReleaseWasTagged("1abc", "1.0"));
//        environment.apply(new ReleaseWasPushed("1abc", "1.0"));
//        environment.apply(new DeployWasStarted("1abc", "1.0"));
//        environment.apply(new DeployWasMade("1abc", "1.0"));
//
//        environment.apply(new ReleaseWasTagged("1abc", "2.0"));
//        environment.apply(new ReleaseWasPushed("1abc", "2.0"));
//        environment.apply(new DeployWasStarted("1abc", "2.0"));
//        environment.apply(new DeployWasMade("1abc", "2.0"));
//
//        deploy = environment.getDeploy("2.0");
//        assertEquals(deploy.release(), environment.currentRelease());
//
//        environment.apply(new DeployWasStarted("1abc", "1.0"));
//        environment.apply(new DeployWasMade("1abc", "1.0"));
//
//        deploy = environment.getDeploy("1.0");
//        assertEquals(deploy.release(), environment.currentRelease());
//    }
//
//    @Test(expected = ReleaseNotFoundException.class)
//    public void itFailsTryingToPushNonExistingRelease2() {
//        givenEnvironment();
//        environment.apply(new ReleaseWasPushed("1abc", "2.5"));
//        Release release = environment.getRelease("2.5");
//        assertEquals(ReleaseStatus.ready(), release.status());
//    }
//
//    @Test(expected = ReleaseNotFoundException.class)
//    public void itFailsTryingToStartDeployForNonExistingRelease2() {
//        givenEnvironment();
//        environment.apply(new DeployWasStarted("1abc", "2.5"));
//        Release release = environment.getRelease("2.5");
//        assertEquals(ReleaseStatus.ready(), release.status());
//    }
//
//    @Test(expected = DeployNotFoundException.class)
//    public void itFailsTryingToDeployNonExistingRelease2() {
//        givenEnvironment();
//        environment.apply(new DeployWasMade("1abc", "2.5"));
//        Release release = environment.getRelease("2.5");
//        assertEquals(ReleaseStatus.ready(), release.status());
//    }

    private void givenEnvironment() {
        environment = new Environment();
    }
}