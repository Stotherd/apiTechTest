import com.google.gson.Gson;
import org.apache.http.*;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignifydTest {

    final private String baseUrl = "https://639b47d1d5141501975125d9.mockapi.io/api/v2/";
    final private RequestUtilities requtil = new RequestUtilities(baseUrl);
    final private Gson gsonParser = new Gson();
    private HttpResponse setupInvestigationResponse;
    private Investigation setupInvestigation;

    public Date convertStringToDate(String data) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return sf.parse(data);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public int countInvestigations(Investigations investigations) {
        int count = 0;
        for (Investigation ignored : investigations.investigations) {
            count++;
        }
        return count;
    }

    @Before
    public void setup() throws IOException {
        //All tests can use at least one investigation added to the list so lets do that
        setupInvestigationResponse = requtil.createInvestigation("test", "1234");
        setupInvestigation = gsonParser.fromJson(EntityUtils.toString(setupInvestigationResponse.getEntity()),
                Investigation.class);
    }

    @After
    public void teardown() throws IOException {
        //All tests can use at least one investigation added to the list so lets do that
        requtil.deleteInvestigation(setupInvestigation.investigationId);
    }

    @Test
    public void getAllExistingInvestigations() throws IOException {
        //Get all existing investigations and verify they match the schema.
        HttpResponse response = requtil.getInvestigations("");
        Investigations investigations = gsonParser.fromJson(EntityUtils.toString(response.getEntity()),
                Investigations.class);
        int count = countInvestigations(investigations);
        //Verify list is the same size as reported
        Assert.assertEquals(investigations.totalResults, count);
        //Verify Status Code
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void getAllExistingInvestigationsPageLimit() throws IOException {
        //Get all existing investigations with a page limit and the length matches the page limit.
        //Assumes more than 2 entries.
        int size_of_page = 2;
        HttpResponse response = requtil.getInvestigations("?limit=" + size_of_page + "&page=1");

        Investigations investigations = gsonParser.fromJson(EntityUtils.toString(response.getEntity()),
                Investigations.class);
        int count = countInvestigations(investigations);
        //Verify list is the same size as reported
        Assert.assertEquals(size_of_page, count);
        //Verify Status Code
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
    @Test
    public void getAllExistingInvestigationsSortByCreatedDate() throws IOException {
        //Get all existing investigations and ensure created date is in reverse order.
        //Requires 3 or more entries
        HttpResponse response = requtil.getInvestigations("?sortBy=createdAt&order=desc");

        Investigations investigations = gsonParser.fromJson(EntityUtils.toString(response.getEntity()), Investigations.class);
        int totalResult = investigations.totalResults;
        //loop through all investigations and verify the created date is a date and is decreasing
        for(int i = 0; i<totalResult-1; i++) {
            Date dateFirst = convertStringToDate(investigations.investigations[i].createdAt);
            Date dateSecond = convertStringToDate(investigations.investigations[i+1].createdAt);
            Assert.assertTrue(dateFirst.compareTo(dateSecond) > 0);
        }
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
//
//
//    @Test
//    public void getAllExistingInvestigationsSortByName() {
//        //Get all existing investigations and ensure name is in alphabetical order
//    }
//
//    @Test
//    public void getAllExistingInvestigationsSortByID() {
//        //Get all existing investigations and ensure id is in order.
//    }
//
//    @Test
//    public void getAllExistingInvestigationsSortByAmount() {
//        //Get all existing investigations and ensure Amount is in order.
//    }
//
    @Test
    public void createInvestigation() {
        Assert.assertEquals(201, setupInvestigationResponse.getStatusLine().getStatusCode());
    }
    @Test
    public void createInvestigationMissingName() throws IOException {
        //This test fails. Currently a randomly selected name is inserted into the required name field.
        HttpResponse response = requtil.createInvestigation(null, "1234");
        Assert.assertEquals("\"Unexpected Error\"", EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
    }
    @Test
    public void getIndividualInvestigation() throws IOException {
        HttpResponse response = requtil.getInvestigation(setupInvestigation.investigationId);
        Investigation investigation = gsonParser.fromJson(EntityUtils.toString(response.getEntity()), Investigation.class);
        Assert.assertEquals(setupInvestigation.investigationId, investigation.investigationId);
        //Verify Status Code
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
    @Test
    public void getIndividualInvestigationThatDoesntExist() throws IOException {
        //This test fails - instead of getting a "not found" error we get a JSON error.
        //We assume 1 does not exist
        HttpResponse response = requtil.getInvestigation("1");
        Assert.assertEquals("\"Not found\"", EntityUtils.toString(response.getEntity()));
        //Verify Status Code
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void updateInvestigation() throws IOException {
        HttpResponse response = requtil.updateInvestigation(setupInvestigation.investigationId,
                "test_updated", "1234546");
        Investigation inv = gsonParser.fromJson(EntityUtils.toString(response.getEntity()),
                Investigation.class);
        Assert.assertEquals(setupInvestigation.investigationId, inv.investigationId);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
    @Test
    public void updateInvestigationThatDoesntExist() throws IOException {
        //We assume 1 does not exist, however we could add a method to identify those that exist and use the first that doesn't
        HttpResponse response = requtil.updateInvestigation("1", "test", "1234");
        Assert.assertEquals("\"Not found\"", EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }
    @Test
    public void updateInvestigationNoId() throws IOException {
        HttpResponse response = requtil.updateInvestigation("", "test", "1234");
        Assert.assertEquals("{\"msg\":\"Invalid request\"}",
                EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
    }
    @Test
    public void updateInvestigationMissingName() throws IOException {
        //This test fails as the request actually succeeds, however a
        // required field is missing so the request should return an error state
        HttpResponse response = requtil.updateInvestigation(setupInvestigation.investigationId,
                null, "1234");
        Assert.assertEquals("{\"msg\":\"Invalid request\"}",
                EntityUtils.toString(response.getEntity()));
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
    }
    @Test
    public void deleteInvestigation() throws IOException {
        HttpResponse response = requtil.deleteInvestigation(setupInvestigation.investigationId);

        Assert.assertEquals(EntityUtils.toString(response.getEntity()), "{}");
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
    @Test
    public void deleteInvestigationIdDoesntExist() throws IOException {
        //Again we assume 1 does not and will not exists, however we could scan the api for an invalid id.
        HttpResponse response = requtil.deleteInvestigation("1");
        Assert.assertEquals(EntityUtils.toString(response.getEntity()), "\"Not found\"");
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }
    @Test
    public void deleteInvestigationNoId() throws IOException {
        //Again we assume 1 does not and will not exists, however we could scan the api for an invalid id.
        HttpResponse response = requtil.deleteInvestigation("");
        Assert.assertEquals(EntityUtils.toString(response.getEntity()),
                "{\"msg\":\"Invalid request\"}");
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
    }
}
