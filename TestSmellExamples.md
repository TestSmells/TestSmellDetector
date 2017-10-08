# Test Smell Examples

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

