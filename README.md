# GreetingAPI
The greeting API has one endpoint that validates the JWT token and responds with a greeting message addressing the user by name


**Endpoint: ** 

### Get /validate

Validate JWT token . Accepts header with key *authorization* and value **Bearer ``<Access-Token>``**

Following is the response object for verified JWT token
```
{
	"name" : "text",
	"expirationTime" : "long value in milli seconds"
  
 }
```

*Note* : 
* For invalid token api sends Http Status response code of 401 Unauthorize 
* For invalid header api sends Http Status response code of 204 SC_NO_CONTENT 
* For other exceptions api sends Http Status response code of 400 SC_BAD_REQUEST
