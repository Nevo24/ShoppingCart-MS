# shoppingcart-checkout Microservice 
The below documentation explains the maven modules in this shoppingcart-checkout microservice.

# Modules

# shoppingcart-checkout-client-kit
This module contains only the POM file to generate the client kit using the YAML file in
shoppingcart-checkout-api. Note that this module is not used by consumers of the service (which copy the API) but 
may be used when testing the service.

# shoppingcart-checkout-external-api
This module aggregates modules that create the client kit to access external services. It contains one 
module for each external service with the API, and a module external-client-kit that generates the client 
kits for the APIs. 

# shoppingcart-checkout-parent
This module manages the dependencies for checkout. It is the parent for other modules.

# shoppingcart-checkout-resourcegen
This module contains only the POM file to generate the server-side code for the services 
defined in the YAML file found in shoppingcart-checkout-api module.

# shoppingcart-checkout-services
This module contains the service (domain) implementation. This also contains the  DTO interfaces
under the *.repository.dto.* package. The implementation of these interfaces can be found in 
shoppingcart-checkout-persistence. The mappers in the shoppingcart-checkout-resources uses these interfaces to map
the resource models to the service models.

 Running the application
The application can be started using the executable JAR in shoppingcart-checkout-exe/target after running the Maven build.

> java -jar shoppingcart-checkout-exe/target/shoppingcart-checkout-exe-1.0.0-SNAPSHOT.jar

In order to run checkout, you also need to run shoppingcart and productorder

> java -jar shoppingcart-exe/target/shoppingcart-exe-1.0.0-SNAPSHOT.jar --server.port=8081
> java -jar productorder-exe/target/productorder-exe-1.0.0-SNAPSHOT.jar --server.port=8082

Note that each application has configuration for Couchbase. You may need to over-ride the default configuration
for your testing. For example, to change the Couchbase configuration for shoppingcart add these options:

> java \
-Dcouchbase.providers[0].env.clusterUrl=127.0.0.1 \
-Dcouchbase.providers[0].env.bucketName=com.amdocs.ms.shoppingcart \
-Dcouchbase.providers[0].name=cbProvider \
-jar shoppingcart-exe/target/shoppingcart-exe-1.0.0-SNAPSHOT.jar --server.port=8081


# Create ShoppingCart
You must create a ShoppingCart to checkout.

curl -i -X POST http://127.0.0.1:8081/shoppingcart-management/v1/shopping-carts?salesChannel= -H "Content-Type:application/json" -d @- << EOF
{
    "shoppingCartItem":
       [
           {
               "state":"ReadyForCheckout",
               "quantity":"1",
               "productOffering":
                  {
                       "name":"Apple iPhone 7",
                       "id":"IPHONE_7"
                  }
       }
       ]
}
EOF

The "id" returned in the response should be substituted in place of "ShoppingCart_1" in the tests below.

# Get Checkout
Verify the Checkout.

curl -i -X GET http://127.0.0.1:8080/checkout-management/v1/checkouts/Checkout_1 -H "Content-Type:application/json"

# Update Checkout
Confirm the Checkout by updating the state.

curl -i -X PATCH http://127.0.0.1:8080/checkout-management/v1/checkouts/Checkout_1 -H "Content-Type:application/json" -d @- << EOF
[
  {
    "op": "replace",
    "path": "/state",
    "value": "Confirmed"
  }
]
EOF

# POST specifies unknown Shopping Cart

curl -i -X POST http://127.0.0.1:8080/checkout-management/v1/checkouts -H "Content-Type:application/json" -d @- << EOF
{
       "id":"ShoppingCart_XXX"
}
EOF

# PUT Specifies unknown Checkout

curl -i -X PUT http://127.0.0.1:8080/checkout-management/v1/checkouts/Checkout_XXX -H "Content-Type:application/json" -d @- << EOF
{
  "href": null,
  "rel": null,
  "id": "Checkout_XXX",
  "state": "Confirmed",
  "validFor": null,
  "relatedParty": [],
  "shoppingCart": {
    "href": null,
    "rel": null,
    "id": "ShoppingCart_1"
  },
  "productOrder": null
}
EOF


# PUT Update specifies bad reference

curl -i -X PUT http://127.0.0.1:8080/checkout-management/v1/checkouts/Checkout_1 -H "Content-Type:application/json" -d @- << EOF
{
  "href": null,
  "rel": null,
  "id": "Checkout_1",
  "state": "Confirmed",
  "validFor": null,
  "relatedParty": [],
  "shoppingCart": {
    "href": null,
    "rel": null,
    "id": "ShoppingCart_XXX"
  },
  "productOrder": null
}
EOF

# PATCH Specifies bad state 

curl -i -X PATCH http://127.0.0.1:8080/checkout-management/v1/checkouts/Checkout_1 -H "Content-Type:application/json" -d @- << EOF
[
  {
    "op": "replace",
    "path": "/state",
    "value": "XXXX"
  }
]
EOF

### PATCH Specifies non-numeric If-Match

curl -i -X PATCH http://127.0.0.1:8080/checkout-management/v1/checkouts/Checkout_1 -H "If-Match:XXX" -H "Content-Type:application/json" -d @- << EOF
[
  {
    "op": "replace",
    "path": "/state",
    "value": "Confirmed"
  }
]
EOF
