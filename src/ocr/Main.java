
package ocr;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main
{
    // **********************************************
    // *** Update or verify the following values. ***
    // **********************************************

    // Replace the subscriptionKey string value with your valid subscription key.
    public static final String subscriptionKey = "8577929937284b2fbe364014ab4f9d25";

    // Replace or verify the region.
    //
    // You must use the same region in your REST API call as you used to obtain your subscription keys.
    // For example, if you obtained your subscription keys from the westus region, replace
    // "westcentralus" in the URI below with "westus".
    //
    // NOTE: Free trial subscription keys are generated in the westcentralus region, so if you are using
    // a free trial subscription key, you should not need to change this region.
    //
    // Also, if you want to use the celebrities model, change "landmarks" to "celebrities" here and in
    // uriBuilder.setParameter to use the Celebrities model.
    public static final String uriBase = "https://westus.api.cognitive.microsoft.com/vision/v1.0/ocr";


    public static void main(String[] args)
    {
       try {
    	   
    	  for(int i = 1; i <= 122; i++ )
		     doRecognize(i);
	   } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		System.out.println(e.getMessage());
	  }
    }
    
    
   static void doRecognize(int count) throws InterruptedException{
	   
    	 HttpClient httpClient = new DefaultHttpClient();

         try
         {
             // NOTE: You must use the same location in your REST call as you used to obtain your subscription keys.
             //   For example, if you obtained your subscription keys from westus, replace "westcentralus" in the
             //   URL below with "westus".
             URIBuilder uriBuilder = new URIBuilder(uriBase);

             uriBuilder.setParameter("language", "en");
             uriBuilder.setParameter("detectOrientation ", "true");

             // Request parameters.
             URI uri = uriBuilder.build();
             HttpPost request = new HttpPost(uri);

             // Request headers.
             request.setHeader("Content-Type", "application/json");
             request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

             // Request body.
             StringEntity requestEntity =
                     new StringEntity("{\"url\":\"https://gautambaghel.github.io/ocr_test_images/"+count+".jpg\"}");
             request.setEntity(requestEntity);

             // Execute the REST API call and get the response entity.
             HttpResponse response = httpClient.execute(request);
             HttpEntity entity = response.getEntity();

             if (entity != null)
             {
                 // Format and display the JSON response.
                 String jsonString = EntityUtils.toString(entity);
                 JSONObject json = new JSONObject(jsonString);
                 String jsonResult = json.toString(2);
                 System.out.println("REST Response:\n");
                 System.out.println(jsonResult);
                 writeInThisFile(count+".json",jsonResult);
                 writeInThisFile(count+".txt",processJSON(jsonResult));
             }
         }
         catch (Exception e)
         {
             // Display error message.
             System.out.println(e.getMessage());
         } finally {
        	 Thread.sleep(5000);
         }
    }
   
   private static String processJSON(String jsonStr) {

	   String result = "";
	   try {
           JSONObject rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject
           JSONArray regions = rootObject.getJSONArray("regions"); // Get all JSONArray rows

           for(int i=0; i < regions.length(); i++) { // Loop over each each row
               JSONObject region = regions.getJSONObject(i); // Get row object
               JSONArray lines = region.getJSONArray("lines"); // Get all elements for each row as an array

               for(int j=0; j < lines.length(); j++) { // Iterate each element in the elements array
                   JSONObject line =  lines.getJSONObject(j); // Get the element object
                   JSONArray words = line.getJSONArray("words"); // Get duration sub object
                   
                   for(int k=0; k < words.length(); k++){
                	   JSONObject word = words.getJSONObject(k);
                	   result += word.getString("text") + " ";
                   }
                   result += "\n";
               }
               result += "\n\n";
           }
           
       } catch (JSONException e) {
           // JSON Parsing error
           e.printStackTrace();
       } 
	   
       return result;
   }
   
   private static void writeInThisFile(String fileName, String data){
	   try{
		    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		    writer.print(data);
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
   }
}
