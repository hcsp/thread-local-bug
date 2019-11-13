# 高级多线程练习：ThreadLocal bug

你维护的一个Spring Boot项目收到用户反馈，有的时候会发生串号问题，即用户在没有登录的情况下莫名其妙地看到其他用户的信息。

隔壁老王告诉你，这是因为项目中的ThreadLocal使用上的一个bug。他为此写了一个[集成测试](https://github.com/hcsp/thread-local-bug/blob/master/src/test/java/com/github/hcsp/UserLoginIntegrationTest.java)用来重现该问题，请找到其中的问题并修复之。

注意，集成测试是没有问题的，bug在Spring Boot主项目中。你可以直接debug该集成测试，寻找bug。

如果你想要启动项目的话，需要先运行`mvn flyway:migrate`把表结构灌入数据库中。

在提交Pull Request之前，你应当在本地确保所有代码已经编译通过，并且通过了测试(`mvn clean test`)

-----
注意！我们只允许你修改以下文件，对其他文件的修改会被拒绝：
- [src/main/java/com/github/hcsp/](https://github.com/hcsp/thread-local-bug/blob/master/src/main/java/com/github/hcsp/)
-----


完成题目有困难？不妨来看看[写代码啦的相应课程](https://xiedaimala.com/tasks/9bf0fb20-929d-4e17-891a-4673291d74a0)吧！

回到[写代码啦的题目](https://xiedaimala.com/tasks/9bf0fb20-929d-4e17-891a-4673291d74a0/quizzes/1b0fc390-74ad-4f55-b355-90b8a9154cc5)，继续挑战！ 
