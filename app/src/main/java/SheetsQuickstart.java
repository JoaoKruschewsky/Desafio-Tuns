
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class SheetsQuickstart {
  private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  /**
   * Prints the names and majors of students in a sample spreadsheet:
   * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
   */

  public static void ReadSpreadsHeets() throws IOException, GeneralSecurityException {
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final String spreadsheetId = "1oodTO1Y_XxOhDpAc7AEAZH1w-vRYXDKAcCwJfJpZFK4";
    final String range = "Desafio!A4:H27";

    Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

    ValueRange response = service.spreadsheets().values()

        .get(spreadsheetId, range)
        .execute();
    List<List<Object>> values = response.getValues();
    if (values == null || values.isEmpty()) {
      System.out.println("No students found.");
    } else {
      System.out.println("Matrícula, Aluno, Faltas, P1, P2, P3, Situacao, Nota para Aprovacao");
      for (List row : values) {
        // Print columns A and E, which correspond to indices 0 and 4.
        System.out.printf("%s, %s,%s, %s,%s,%s", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4),
            row.get(5));
      }
    }
  }

  public static void CalculateGradeAndLack() throws IOException, GeneralSecurityException {

    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final String spreadsheetId = "1oodTO1Y_XxOhDpAc7AEAZH1w-vRYXDKAcCwJfJpZFK4";
    final String range = "Desafio!A4:H27";
    List<Integer> students = new ArrayList<>(); // List to save student absences
    List<List<Float>> grade = new ArrayList<>(); /*
                                                  * List to save the grades, which are P1, P2, P3. Both lists are
                                                  * to retrieve the values of the first line
                                                  * and calculate the average
                                                  */
    List<String> studentList = new ArrayList<>(); // Array of student names

    // Connecting with the API could put this in a class but I did it like this anyway
    Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

    ValueRange response = service.spreadsheets().values()

        .get(spreadsheetId, range)
        .execute();
    List<List<Object>> values = response.getValues();
    if (values == null || values.isEmpty()) {
      System.out.println("No data found.");
    } else {
      for (List row : values) {
        // First assign the values of each column p1,p2,p3 to 3 variables respectively
        Float P1 = Float.parseFloat(row.get(3).toString()) / 10.0f;
        Float P2 = Float.parseFloat(row.get(4).toString()) / 10.0f;
        Float P3 = Float.parseFloat(row.get(5).toString()) / 10.0f;
        grade.add(List.of(P1, P2, P3)); // Here it adds to the grade List

        students.add(Integer.parseInt(row.get(2).toString())); // Here I get the values from the Absence column

      }
      // For loop to read each index in the grade list, which would be the indices of the outer list
      for (int i = 0; i < grade.size(); i++) {
        List<Float> row = grade.get(i);       // For loop to read each index in the grade list, which would be the indices of the outer list

        float average = 0; // Variable to calculate the average


        // For para ler os valores da list interna e fazer o calculo
        for (Float value : row) {
          average += value / 3; // calculo

        }
        int value = students.get(i); // Variable to get the values from the Absence column

              // Validations for challenge rules

        if (value > 15 && average >= 5 && average < 7) {
          String studentName = values.get(i).get(1).toString();// Getting the name of the student from the spreadsheet
          studentList.add(studentName); // adding to the list of student names
          System.out.println("Reprovacao por fnaf: " + studentName + " e sua nota foi: " + (int) Math.ceil(average));         
           float naf = (int) Math.ceil(10 - average);// NAF calculation
          System.out.println("Nota para Aprovacao Final (NAF) para " + studentName + ": " + naf);

        } else if (value > 15) {
          String studentName = values.get(i).get(1).toString();
          studentList.add(studentName);
          System.out.println("Reprovacao por Falta  " + studentName + " Quantidade de faltas: " + value + " e sua nota foi: "
              + (int) Math.ceil(average));
        } else if (average < 5) {
          String studentName = values.get(i).get(1).toString();
          studentList.add(studentName);
          System.out.println("Aluno Reprovado por nota: " + studentName + " Sua nota foi: " + (int) Math.ceil(average));
        } else if (average >= 7) {
          String studentName = values.get(i).get(1).toString(); // + "Aprovado"; // Adding "Approved" to the student's name, would be to fill in the spreadsheet
    System.out.println("Aluno Aprovado por nota: " + studentName + " Sua nota foi: " + (int) Math.ceil(average));


    /*  Here I start the attempt to add the results to the spreadsheet
       Since I tried and couldn't, I'll leave only this code snippet here
        Get the current values of the "Status" column
          
        I was getting a 403 error and couldn't resolve it.  */

// Code to fill the spreadsheet    
    /*   final String rangeNew = "Desafio!G4:G27";
    ValueRange updateValue = service.spreadsheets().values().get(spreadsheetId, rangeNew).execute();
    List<List<Object>> currentValues = updateValue.getValues();

    
        // If the column is empty, prepare new values with the status "Approved"
        List<List<Object>> newValues = new ArrayList<>();
        for (int j = 0; j < 27; j++) {
            List<Object> newRow = new ArrayList<>();
            newRow.add("Aprovado");
            newValues.add(newRow);
        }

        // Update the "Status" column with the new values
        ValueRange newValue = new ValueRange().setValues(newValues);
        UpdateValuesResponse response2 = service.spreadsheets().values()
                .update(spreadsheetId, rangeNew, newValue)
                .setValueInputOption("RAW")
                .execute(); */
     

        } else if (average >= 5 && average < 7) {
          String studentName = values.get(i).get(1).toString();
          studentList.add(studentName);
          System.out.println("Aluno para Exame Final: " + studentName + " Sua nota foi: " + (int) Math.ceil(average));
        }

      }

    }

  }

  public static void main(String... args) throws IOException, GeneralSecurityException {
    // Build a new authorized API client service.
     //ReadSpreadsHeets();
    CalculateGradeAndLack();
  }
}
