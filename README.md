# HTTP Request and Response Flow

## Steps

1. **Client sends HTTP Request**  
   The client sends an HTTP request to the server.

2. **Server checks for welcome file**  
   Since no resource path is provided in the URL, the server looks for the `welcome-file-list` inside `web.xml`.

3. **Server responds with welcome file**  
   The server sends a response containing the content of the welcome file.

4. **Client executes response code**  
   The client accepts the response and executes the code.

5. **Initialization method triggered**  
   The code contains a fragment indicating a method should be called during initialization.

6. **New request sent**  
   The initialization method is called, which sends another request to the server.

7. **Server processes doctor request**  
   The server accepts the request and invokes a Java method to retrieve all doctors.

8. **Doctors retrieved and sent**  
   The doctors are extracted from the DAO, added to the response, and sent back to the client.

9. **Client updates UI**  
   The client accepts the response, and JavaScript modifies the HTML to display the doctors.

API
http://localhost:8080/healthcare/admin/doctors.html
