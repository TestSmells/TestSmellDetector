# Test Smell Examples

Provided below are examples of test smells that were detected in open source Android projects.

 

#### Lazy Test

##### Source

App: [com.github.marmalade.aRevelation](https://github.com/MarmaladeSky/aRevelation)

Test File: [CryptographerTest.java](https://github.com/MarmaladeSky/aRevelation/blob/950309c589d55b3fc8c879d548bae2224d558668/src/test/java/com/github/marmaladesky/tests/CryptographerTest.java)

Production File: [Cryptographer.java](https://github.com/MarmaladeSky/aRevelation/blob/bd06e012072a805386fbf222c479024b15dc13c0/src/main/java/com/github/marmaladesky/Cryptographer.java)

##### Rationale

Both test methods, `testDecrypt()` and `testEncrypt()`, call the same SUT method, `Cryptographer.decrypt()`

##### Code Snippet

```java
    @Test
    public void testDecrypt() throws Exception {
            FileInputStream file = new FileInputStream(ENCRYPTED_DATA_FILE_4_14);
            byte[] enfileData = new byte[file.available()];
            FileInputStream input = new FileInputStream(DECRYPTED_DATA_FILE_4_14);
            byte[] fileData = new byte[input.available()];
            input.read(fileData);
            input.close();
            file.read(enfileData);
            file.close();
            String expectedResult = new String(fileData, "UTF-8");
            assertEquals("Testing simple decrypt",expectedResult, Cryptographer.decrypt(enfileData, "test"));
    }

    @Test
    public void testEncrypt() throws Exception {
            String xml = readFileAsString(DECRYPTED_DATA_FILE_4_14);
            byte[] encrypted = Cryptographer.encrypt(xml, "test");
            String decrypt = Cryptographer.decrypt(encrypted, "test");
            assertEquals(xml, decrypt);
    }
```



#### Assertion Roulette

##### Source

App: [com.madgag.agit](https://github.com/rtyley/agit)

Test File: [GitAsyncTaskTest.java](https://github.com/rtyley/agit/blob/fc99d8eaa42940198589b032a2b9ba74d9ce3094/agit-integration-tests/src/main/java/com/madgag/agit/GitAsyncTaskTest.java)

Production File: [GitAsyncTask.java](https://github.com/rtyley/agit/blob/e42190e0f31f3d28086616f782e7f31422e9d229/agit/src/main/java/com/madgag/agit/operations/GitAsyncTask.java)

##### Rationale

The `assertThat()` method is called 3 times within the test method. Each assert statement checks for a different condition, but the developer does not provide a explanation message for each assert statement. Hence, if one of the assert statements were to fail, identifying the cause of the failure is not straightforward. 

##### Code Snippet

```java
    @MediumTest
    public void testCloneNonBareRepoFromLocalTestServer() throws Exception {
        Clone cloneOp = new Clone(false, integrationGitServerURIFor("small-repo.early.git"), helper().newFolder());

        Repository repo = executeAndWaitFor(cloneOp);

        assertThat(repo, hasGitObject("ba1f63e4430bff267d112b1e8afc1d6294db0ccc"));

        File readmeFile = new File(repo.getWorkTree(), "README");
        assertThat(readmeFile, exists());
        assertThat(readmeFile, ofLength(12));
    }
```



#### Conditional Test Logic

##### Source

App: [btools.routingapp](https://github.com/abrensch/brouter)

Test File: [LinkedListContainerTest.java](https://github.com/abrensch/brouter/blob/ccf6641bad1c8c0b50b3021aa9a75588f846df7f/brouter-codec/src/test/java/btools/codec/LinkedListContainerTest.java)

Production File: [LinkedListContainer.java](https://github.com/abrensch/brouter/blob/ccf6641bad1c8c0b50b3021aa9a75588f846df7f/brouter-codec/src/main/java/btools/codec/LinkedListContainer.java)

##### Rationale

The test method, `linkedListTest1()`, contains multiple `for` loop statements (i.e. control flow statements). This increases the complexity of the test method and hence has a negative impact on maintenance of the test. 

##### Code Snippet

```java
  @Test
  public void linkedListTest1()
  {
    int nlists = 553;

    LinkedListContainer llc = new LinkedListContainer( nlists, null );

    for ( int ln = 0; ln < nlists; ln++ )
    {
      for ( int i = 0; i < 10; i++ )
      {
        llc.addDataElement( ln, ln * i );
      }
    }

    for ( int i = 0; i < 10; i++ )
    {
      for ( int ln = 0; ln < nlists; ln++ )
      {
        llc.addDataElement( ln, ln * i );
      }
    }

    for ( int ln = 0; ln < nlists; ln++ )
    {
      int cnt = llc.initList( ln );
      Assert.assertTrue( "list size test", cnt == 20 );

      for ( int i = 19; i >= 0; i-- )
      {
        int data = llc.getDataElement();
        Assert.assertTrue( "data value test", data == ln * ( i % 10 ) );
      }
    }

    try
    {
      llc.getDataElement();
      Assert.fail( "no more elements expected" );
    }
    catch (IllegalArgumentException e)
    {
    }
  }
```



#### Empty Test

##### Source

App: [com.actisec.clipcaster](https://github.com/activems/clipcaster)

Test File: [LastPassParserTest.java](https://github.com/activems/clipcaster/blob/38398d1d047a4064a7017ca8f0d0f3ff0782560c/app/src/androidTest/java/com/actisec/clipcaster/parser/LastPassParserTest.java)

Production File: [LastPassParser.java](https://github.com/activems/clipcaster/blob/38398d1d047a4064a7017ca8f0d0f3ff0782560c/app/src/main/java/com/actisec/clipcaster/parser/LastPassParser.java)

##### Rationale

The test method, `testCredGetFullSampleV1()`, contains only comments (i.e. no executable statements). A test method without executable statements will be marked as passing when executed. 

##### Code Snippet

```java
    public void testCredGetFullSampleV1() throws Throwable{
//        ScrapedCredentials credentials =  innerCredTest(FULL_SAMPLE_v1);
//        assertEquals("p4ssw0rd", credentials.pass);
//        assertEquals("user@example.com",credentials.user);

    }
```



#### Exception Catching & Throwing

##### Source

App: [ch.hgdev.toposuite](https://github.com/hgdev-ch/toposuite-android.git)

Test File: [AbrissTest.java](https://github.com/hgdev-ch/toposuite-android/blob/2d76131d26871392ebf2a2c9ac3657592a9549f3/app/src/androidTest/java/ch/hgdev/toposuite/test/calculation/AbrissTest.java)

Production File: [Abriss.java](https://github.com/hgdev-ch/toposuite-android/blob/88d6ca54ba3d42b35e8bea157102d984498eae62/app/src/main/java/ch/hgdev/toposuite/calculation/Abriss.java)

##### Rationale

In this example, the developer fails the test when a specific exception occurs. Ideally, the developer should split this test method into multiple test methods that (1) knowingly generate the exception and (2) do not generate the exception. The developer should utilize the `@Test expected` attribute in JUnit 4 to fail the test when the exception occurs instead of explicitly catching  or throwing the exception.

##### Code Snippet

```java
       @Test
    public void realCase() {
        Point p34 = new Point("34", 556506.667, 172513.91, 620.34, true);
        Point p45 = new Point("45", 556495.16, 172493.912, 623.37, true);
        Point p47 = new Point("47", 556612.21, 172489.274, 0.0, true);
        Abriss a = new Abriss(p34, false);
        a.removeDAO(CalculationsDataSource.getInstance());
        a.getMeasures().add(new Measure(p45, 0.0, 91.6892, 23.277, 1.63));
        a.getMeasures().add(new Measure(p47, 281.3521, 100.0471, 108.384, 1.63));

        try {
            a.compute();
        } catch (CalculationException e) {
            Assert.fail(e.getMessage());
        }

        // test intermediate values with point 45
        Assert.assertEquals("233.2405",
                this.df4.format(a.getResults().get(0).getUnknownOrientation()));
        Assert.assertEquals("233.2435",
                this.df4.format(a.getResults().get(0).getOrientedDirection()));
        Assert.assertEquals("-0.1", this.df1.format(
                a.getResults().get(0).getErrTrans()));

        // test intermediate values with point 47
        Assert.assertEquals("233.2466",
                this.df4.format(a.getResults().get(1).getUnknownOrientation()));
        Assert.assertEquals("114.5956",
                this.df4.format(a.getResults().get(1).getOrientedDirection()));
        Assert.assertEquals("0.5", this.df1.format(
                a.getResults().get(1).getErrTrans()));

        // test final results
        Assert.assertEquals("233.2435", this.df4.format(a.getMean()));
        Assert.assertEquals("43", this.df0.format(a.getMSE()));
        Assert.assertEquals("30", this.df0.format(a.getMeanErrComp()));
    }
```



#### General Fixture

##### Source

App: [at.bitfire.cadroid](https://github.com/bitfireAT/cadroid.git)

Test File: [CertificateInfoTest.java](https://github.com/bitfireAT/cadroid/blob/84cecaf2691dffe6eb1f5c300d9fe52b21d7b536/app/src/androidTest/java/at/bitfire/cadroid/test/CertificateInfoTest.java)

Production File: [CertificateInfo.java](https://github.com/bitfireAT/cadroid/blob/84cecaf2691dffe6eb1f5c300d9fe52b21d7b536/app/src/main/java/at/bitfire/cadroid/CertificateInfo.java)

##### Rationale

The setup/fixture method initializes a total of 6 fields (variables). However, the test method, `testIsCA()`, only utilizes 4 fields.

##### Code Snippet

```java
	protected void setUp() throws Exception {
		assetManager = getInstrumentation().getContext().getAssets();
		certificateFactory = CertificateFactory.getInstance("X.509");
		
		infoDebianTestCA = loadCertificateInfo("DebianTestCA.pem");
		infoDebianTestNoCA = loadCertificateInfo("DebianTestNoCA.pem");
		infoGTECyberTrust = loadCertificateInfo("GTECyberTrustGlobalRoot.pem");
		
		// user-submitted test cases
		infoMehlMX = loadCertificateInfo("mehl.mx.pem");
	}

	public void testIsCA() {
		assertTrue(infoDebianTestCA.isCA());
		assertFalse(infoDebianTestNoCA.isCA());
		assertNull(infoGTECyberTrust.isCA());
		
		assertFalse(infoMehlMX.isCA());
	}
```



#### Mystery Guest

##### Source

App: [com.gmail.walles.johan.batterylogger](https://github.com/walles/batterylogger)

Test File: [SystemStateTest.java](https://github.com/walles/batterylogger/blob/11d81ee721fc12ad324f4a04e265b0ff8c553736/src/androidTest/java/com/gmail/walles/johan/batterylogger/SystemStateTest.java)

Production File: [SystemState.java](https://github.com/walles/batterylogger/blob/0f1a8e8d36ab474930bb2e060e1e319753ef01a3/src/main/java/com/gmail/walles/johan/batterylogger/SystemState.java)

##### Rationale

As part of the test, the test method, `testPersistence()`, creates a File (tempFile) in a specific directory and then utilizes this file in the test process. 

##### Code Snippet

```java
    public void testPersistence() throws Exception {
        File tempFile = File.createTempFile("systemstate-", ".txt");
        try {
            SystemState a = new SystemState(then, 27, false, bootTimestamp);
            a.addInstalledApp("a.b.c", "ABC", "1.2.3");

            a.writeToFile(tempFile);
            SystemState b = SystemState.readFromFile(tempFile);

            assertEquals(a, b);
        } finally {
            //noinspection ConstantConditions
            if (tempFile != null) {
                //noinspection ResultOfMethodCallIgnored
                tempFile.delete();
            }
        }
    }
```



#### Print Statement (Redundant Print)

##### Source

App: [org.hwyl.sexytopo](https://github.com/richsmith/sexytopo)

Test File: [Space3DTransformerTest.java](https://github.com/richsmith/sexytopo/blob/80cd8bbda23fba569ed470d80ed99e6bcbec5159/app/src/test/java/org/hwyl/sexytopo/control/util/Space3DTransformerTest.java)

Production File: [Space3DTransformer.java](https://github.com/richsmith/sexytopo/blob/3569ca475ce93cd6a5d4c1f8c0a6053ea1edcd20/app/src/main/java/org/hwyl/sexytopo/control/util/Space3DTransformer.java)

##### Rationale

The test method, `testTransform10mNEUAndBack()`, contains a statement that prints the value of a variable to the console. This is a redundant statement that might have been added by a developer, for debugging purposes, at the time of writing the test method. 

##### Code Snippet

```java
    @Test
    public void testTransform10mNEUAndBack() {
        Leg northEastAndUp10M = new Leg(10, 45, 45);
        Coord3D result = transformer.transform(Coord3D.ORIGIN, northEastAndUp10M);
        System.out.println("result = " + result);
        Leg reverse = new Leg(10, 225, -45);
        result = transformer.transform(result, reverse);
        assertEquals(Coord3D.ORIGIN, result);
    }
```



#### Redundant Assertion

##### Source

App: [com.litmus.worldscope](https://github.com/nus-mtp/worldscope)

Test File: [LoginActivityTest.java](https://github.com/nus-mtp/worldscope/blob/41770e12e0780b57ad80a48c2a4cd07f57aadfa6/client/android/WorldScope/app/src/androidTest/java/com/litmus/worldscope/LoginActivityTest.java)

Production File: [LoginActivity.java](https://github.com/nus-mtp/worldscope/blob/8856e25896f2794e4f247af395138a435532e8d3/client/android/WorldScope/app/src/main/java/com/litmus/worldscope/LoginActivity.java)

##### Rationale

The test method, `testTrue()`, will always pass as since the assert statement compares a Boolean value of true against another Boolean value of true. 

##### Code Snippet

```java
    @Test
    public void testTrue() {
        assertEquals(true, true);
    }
```

