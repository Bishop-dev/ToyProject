ToyProject
==========
This project demonstrates ability to work with combination of frameworks. At backend side used <a href="http://hibernate.org">Hibernate</a>, <a href="http://spring.io">Spring</a>, <a href="http://docs.spring.io/spring/docs/2.0.8/reference/mvc.html">Spring MVC</a> and <a href="http://projects.spring.io/spring-security/">Spring Security</a>. For testing I used <a href="http://junit.org/">JUnit4</a>, <a href="http://hamcrest.org/">Hamcrest</a>, <a href="https://code.google.com/p/mockito/">Mockito</a> and <a href="http://docs.spring.io/spring/docs/3.2.5.RELEASE/javadoc-api/org/springframework/test/web/servlet/MockMvc.html">MockMVC</a>. <a href="http://en.wikipedia.org/wiki/Create,_read,_update_and_delete">CRUD</a> controller (UserRESTService class) implemented as <a href="http://en.wikipedia.org/wiki/Representational_state_transfer">REST</a> service. Frontend is presented by using <a href="http://jquery.com/">jQuery</a>, <a href="http://jqueryvalidation.org/">jQuery Validation Plugin</a>, <a href="http://backbonejs.org/">Backbone.js</a> (Backbone depends on <a href="http://underscorejs.org/">Underscore</a>) and UI is presented by <a href="http://getbootstrap.com/">Bootstrap</a> and <a href="http://jqueryui.com/">jQuery UI</a>. This application use <a href="http://www.h2database.com/html/main.html">H2 database.
During starting this application Hibernate creates database. Then needed to add defaults users to created tables. You may use this script:

INSERT INTO ROLE (ROLE_ID, ROLE_NAME) VALUES (1, 'admin');

INSERT INTO ROLE (ROLE_ID, ROLE_NAME) VALUES (2, 'user');

INSERT INTO USER (USER_ID, USER_LOGIN, USER_PASSWORD, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME, USER_BIRTHDAY, ROLE_ID) VALUES (1, 'admin', 'admin', 'admin@mail.com', 'Admin', 'Adminovich', '1990-01-01', 1);

INSERT INTO USER (USER_ID, USER_LOGIN, USER_PASSWORD, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME, USER_BIRTHDAY, ROLE_ID) VALUES (2, 'user', 'user', 'user@mail.com', 'User', 'Userovich', '1991-01-01', 2); 

The view of login page:

<a href="http://imgur.com/4akXZd2"><img src="http://i.imgur.com/4akXZd2.jpg" title="Hosted by imgur.com" /></a>

Users could register in this system. Captcha prevents massive automatic registration of robots. Simple users see only welcoming page. Admins may create, modify and delete users. 
