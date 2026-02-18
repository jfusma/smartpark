
Objective:

Design and implement a microservice using Java and Spring Boot to manage an exclusive parking lot for employees. 
The system must monitor space availability and calculate costs.

Required Functionality

The microservice must expose a REST API that enables the following operations:
Register Parking Entry: Receives the license plate (used as the unique identifier) and whether the user requires an electric charger.
 Based on current occupancy, the system decides whether to grant or deny access.

Occupy Space: Using sensors that detect the license plate and the specific occupied space, the system must record this information. Additionally, users are allowed to change spaces during their stay.
Register Exit: When the vehicle leaves the parking lot, the system frees up the space and returns a service summary, including the duration of the stay and the total amount due.
Key Business Rules

Parking Capacity: The parking lot has a total of 100 spaces, 20 of which are equipped with electric chargers.
Base Rate: Parking costs €2.50 per hour (or fraction thereof) for the first three hours, and €2.00 for each additional hour.

Electric Surcharge: If the vehicle occupies a space with an electric charger, a fixed fee of €3.50 is applied for the use of the charger.

Stack:

Java 17

springboot

H2

Swagger
