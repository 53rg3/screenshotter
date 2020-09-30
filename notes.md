# Todo

- Each WebDriver throws a lot of log messages. E.g.

  ```
  Starting ChromeDriver 85.0.4183.87 (cd6713ebf92fa1cacc0f1a598df280093af0c5d7-refs/branch-heads/4183@{#1689}) on port 19663
  Only local connections are allowed.
  Please see https://chromedriver.chromium.org/security-considerations for suggestions on keeping ChromeDriver safe.
  ChromeDriver was started successfully.
  Sep 28, 2020 1:23:27 PM org.openqa.selenium.remote.ProtocolHandshake createSession
  INFO: Detected dialect: W3C
  ```

  How to turn that off?

- And Turn off Hibernate logging messages. Hibernate uses Log4j. Import, set log4j2.properties to >info

- Mmmh... Akka throws when we run from JAR?

  ```
  [ERROR] [09/30/2020 18:00:09.191] [ManagingActor-akka.actor.internal-dispatcher-5] [akka://ManagingActor/user] configuration problem while creating [akka://ManagingActor/user/479e7655-9f2d-4ce3-997a-a0a2d38d7fc2] with dispatcher [akka.actor.default-dispatcher] and mailbox [akka.actor.typed.default-mailbox]
  akka.ConfigurationException: configuration problem while creating [akka://ManagingActor/user/479e7655-9f2d-4ce3-997a-a0a2d38d7fc2] with dispatcher [akka.actor.default-dispatcher] and mailbox [akka.actor.typed.default-mailbox]
  ...
  Caused by: akka.ConfigurationException: Mailbox Type [akka.actor.typed.default-mailbox] not configured
  	at akka.dispatch.Mailboxes.lookupConfigurator(Mailboxes.scala:219)
  	at akka.dispatch.Mailboxes.lookup(Mailboxes.scala:91)
  	at akka.dispatch.Mailboxes.getMailboxType(Mailboxes.scala:178)
  	at akka.actor.LocalActorRefProvider.actorOf(ActorRefProvider.scala:695)
  ```

  Doesn't happen in tests. Run from IDE for now.
