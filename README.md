# Revolut Money Transfer Test #   

Simple standalone application with HTTP REST API for transferring money between account with embedded database
### 1. How to start? ###
Run `mvn clean install` to compile application and run tests
 
Run `com.dada.revolut.Application` to launch application. It will create H2 database and 
will start server on `8080` port.  
### 2. API ###
All calls to API must be started with `http://localhost:8080`
                                                        
<table>
<thead>
<tr>
<th>Endpoint</th>
<th>Description</th>
<th>Parameters</th>
<th>Success Response</th>
</tr>
</thead>
<tbody>
<tr>
	<td><code>POST /account</code></td>
	<td>Creates new account</td>
	<td>-</td>
	<td>
      <pre>
{
 "id": ACCOUNT_ID,
 "balance": 0
}
	  </pre>
    </td>
</tr>
<tr>
	<td><code>GET /account/{id}</code></td>
	<td>Gets account by ID</td>
    <td>Path:<br/><code>id</code> - account ID</td>
	<td>
      <pre>
{
  "id": id,
  "balance": 0
}
	  </pre>
    </td>
</tr>
<tr>
	<td><code>POST /account/{id}/deposit</code></td>
	<td>Deposits specified amount of money on account</td>
    <td>
    	Path:<br/><code>id</code> - account ID<br/>
        Body:
        <pre>
{
  "amount": 100
}
        </pre>
    </td>
	<td>
      Updated balance
    </td>
</tr>
<tr>
	<td><code>POST /account/{id}/withdraw</code></td>
	<td>Withdraws specified amount of money on account</td>
    <td>
    	Path:<br/><code>id</code> - account ID<br/>
        Body:
        <pre>
{
  "amount": 50
}
        </pre>
    </td>
	<td>
      Updated balance
    </td>
</tr>
<tr>
	<td><code>POST /transfer</code></td>
    <td>Tranfers specified amount of money from account <code>fromId</code> to <code>toId</code></td>
    <td>
    	Path:<br/>
        <code>fromId</code> - account to withdraw from<br/>
        <code>toId</code> - account to deposit on<br/>
        Body:
        <pre>
{
	"fromId": "sourceAccountId",
	"toId": "destinationAccountId",
  	"amount": 100
}
        </pre>
    </td>
	<td>
      204 No Content
    </td>
</tr>
</tbody></table>

### 3. 3rd-party components used ###
* [SparkJava](http://sparkjava.com/) - A micro framework for creating web applications
* [H2 Database Engine](http://www.h2database.com/html/main.html) - Java SQL file and in-memory database
* [Guice](https://github.com/google/guice) - lightweight dependency framework
* [Lombok](https://projectlombok.org) - bytecode generation library to avoid of writing of the boilerplate code
* [sql2o](https://www.sql2o.org/) - small Java library, that makes it easy to execute sql statements against your JDBC compliant database
* [jackson](https://github.com/FasterXML/jackson) - json to/from POJO library
