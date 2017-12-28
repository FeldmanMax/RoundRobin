_**Introduction**_

Weighted Round Robin is a project which enables to balance between different endpoints.

This code was **NEVER** tested on production so please use with **CAUTION** 

_**Configuration**_

In the unit test folder under the resources you will find several connection which i have prepared and tested.

Example 1:

    {
        "info": {
          "name": "search_google",
          "isUsingConnections": false
        },
        "endpointsList": [
          {
            "name": "google_com",
            "value": "http://www.google.com"
          },
          {
            "name": "google_th",
            "value": "http://www.google.co.th"
          }
        ]
    }
    
    In this example we see a connection with the name: search_google
    which has 2 endpoints www.google.com and www.google.co.th
    
    
Example 2:
    
    {
        "info": {
          "name": "search_engines",
          "isUsingConnections": true
        },
        "endpointsList": [
          {
            "name": "google",
            "value": "search_google"
          },
          {
            "name": "microsoft",
            "value": "search_bing"
          }
        ]
    }
    
    Please note that this connection uses as a value another connection
    You can set it up by setting "isUsingConnections": true and as endpoint value
    supply the name of another connection.
    In our case, we have 2 connection which are "google" and "microsoft"
    Google has 2 endpoints
    Microsoft has 1 endpoint