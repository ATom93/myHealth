package rocketmen.myapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity {

    public static String birthDate = "0/0/0";
    User user;
    String globalAddress = "http://adaptapppoliba.altervista.org/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        user = new User();

        MemoryManager memoryManager = new MemoryManager(this);

        if(!memoryManager.readInMemory("firstLogin").equals("false")){
            resetCache();
        }

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        int year;
        int month;
        int day;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            this.year = year;
            this.month = month +1;
            this.day = day;

            birthDate = Integer.toString(this.year)+"/"+Integer.toString(this.month)+"/"+Integer.toString(this.day);
            Toast.makeText(getContext(), birthDate,Toast.LENGTH_LONG).show();
        }
    }

    public void openRegisterForm(View v){
        setContentView(R.layout.register);
    }

    public void openRegisterForm2(View v){

        EditText edName = (EditText) findViewById(R.id.editText2);
        EditText edSurname = (EditText) findViewById(R.id.editText3);
        EditText edAltezza = (EditText) findViewById(R.id.editText6);
        RadioButton rbMale = (RadioButton)findViewById(R.id.radioButton3);
        RadioButton rbFemale = (RadioButton)findViewById(R.id.radioButton4);
        String name = edName.getText().toString();
        String surname = edSurname.getText().toString();
        String altezza = edAltezza.getText().toString();
        Sex sex = null;

        if(name.equals("")){
            Toast.makeText(this,"Inserire nome",Toast.LENGTH_SHORT).show();
        }else {
            if(surname.equals("")){
                Toast.makeText(this,"Inserire cognome",Toast.LENGTH_SHORT).show();
            }
            else {
                if(!rbMale.isChecked() && !rbFemale.isChecked()){
                    Toast.makeText(this,"Selezionare sesso",Toast.LENGTH_SHORT).show();
                }
                else {

                    if(rbMale.isChecked()){
                        sex = Sex.MALE;
                    }

                    if(rbFemale.isChecked()){
                        sex = Sex.FEMALE;
                    }

                    if(birthDate.equals("0/0/0")){
                        Toast.makeText(this,"Selezionare data di nascita",Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if(altezza.equals("")){
                            Toast.makeText(this, "Inserire altezza", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            user.setName(name);
                            user.setSurname(surname);
                            user.setBirthDate(birthDate);
                            user.setSex(sex);
                            user.setAltezza(altezza);
                            //startThings();
                            setContentView(R.layout.register2);
                        }

                    }
                }
            }
        }





    }

    public void createUser(View v){

        EditText edUsername = (EditText)findViewById(R.id.editText2);
        EditText edPassword = (EditText)findViewById(R.id.editText3);
        EditText edPassword2 = (EditText)findViewById(R.id.editText4);

        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();
        String password2 = edPassword2.getText().toString();

        if(username.equals("")){
            Toast.makeText(this,"Inserire username",Toast.LENGTH_SHORT).show();
        }
        else{
            if(password.equals("")){
                Toast.makeText(this,"Inserire password",Toast.LENGTH_SHORT).show();
            }
            else{
                if(password2.equals("")){
                    Toast.makeText(this,"Reinserire password",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!password.equals(password2)){
                        Toast.makeText(this,"Le password non coincidono",Toast.LENGTH_SHORT).show();
                    }
                    else {

                        user.setUsername(username);
                        user.setPassword(password);

                        String address = globalAddress + "/progetto/crea_utenti.php";
                        String payload = "name="+user.getName()+
                                "&surname="+user.getSurname()+
                                "&sex="+user.getSex()+
                                "&birthdate"+user.getBirthDate()+
                                "&altezza="+user.getAltezza()+
                                "&username="+user.getUsername()+
                                "&password="+user.getPassword();

                        HttpRequester requester = new HttpRequester(address,payload,this);
                        requester.execute("");

                        try {
                            String response = requester.get();
                            if(response.startsWith("1")){

                                MemoryManager memoryManager = new MemoryManager(this);
                                memoryManager.writeInMemory("penultimopeso","0");

                                setContentView(R.layout.login);
                            }
                            if(response.startsWith("0")){
                                Toast.makeText(this, "L'username esiste già", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        }

    }

    public void login(View v) throws ExecutionException, InterruptedException, JSONException {

        EditText edUsername = (EditText)findViewById(R.id.editText);
        EditText edPassword = (EditText)findViewById(R.id.editText5);

        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();

        if(username.equals("")){
            Toast.makeText(this,"Inserire username",Toast.LENGTH_SHORT).show();
        }
        else{
            if(password.equals("")){
                Toast.makeText(this,"Inserire password",Toast.LENGTH_SHORT).show();
            }
            else {

                user.setUsername(username);
                MemoryManager memoryManager = new MemoryManager(this);

                memoryManager.writeInMemory("username",username);

                String payload = new Utils().buildStringForLogin(new String[]{username,password});
                String address = globalAddress +"progetto/login.php";

                HttpRequester requester = new HttpRequester(address,payload,this,user);
                requester.execute("");

                if(requester.get().startsWith("2")){

                    Toast.makeText(this, "Login effettuato con successo", Toast.LENGTH_SHORT).show();

                    payload = new Utils().buildStringForGetUser(new String[]{username});
                    address = globalAddress + "progetto/retrive_id_utente.php";

                    requester = new HttpRequester(address,payload,this);
                    requester.execute("");

                    String id = requester.get();
                    user.setId(id);

                    //Client id building for broker subscription
                    String clientId = getResources().getString(R.string.app_name)+user.getUsername();
                    memoryManager = new MemoryManager(this);
                    memoryManager.writeInMemory("clientId",clientId);

                    if(!memoryManager.readInMemory("firstLogin").equals("false")){

                        address = globalAddress + "progetto/retrive_age_utente.php";
                        payload = "username="+username;
                        requester = new HttpRequester(address,payload,this);
                        requester.execute("");
                        String age = requester.get();

                        address = globalAddress + "progetto/retrive_altezza_utente.php";
                        payload = "username="+username;
                        requester = new HttpRequester(address,payload,this);
                        requester.execute("");
                        String altezza = requester.get();

                        address = globalAddress + "progetto/retrive_sesso_utente.php";
                        payload = "username="+username;
                        requester = new HttpRequester(address,payload,this);
                        requester.execute("");
                        String sesso = requester.get();

                        String JSONDaily = "{'id':'"+username+"','age':'"+age+"','altezza':'"+altezza+"','sesso':'"+sesso+"','peso':'0','calorie':'0','passi':'0','acqua':'0','bici':'0'}";
                        String JSONTips = "{'tip0':'Nessun tip','tip1':'Nessun tip','tip2':'Nessun tip','tip3':'Nessun tip','tip4':'Nessun tip','tip5':'Nessun tip'}";
                        //String goal = "{'calorie':4000,'acqua':3,'pesoFormaSup':70,'pesoFormaInf':60}";
                        String goal = new UserHandler(this).calculateGoal(JSONDaily);

                        memoryManager.writeInMemory("goal",goal);
                        memoryManager.writeInMemory("JSONDaily",JSONDaily);
                        memoryManager.writeInMemory("JSONTips",JSONTips);
                        memoryManager.writeInMemory("penultimopeso","0");
                        memoryManager.writeInMemory("firstLogin","false");

                    }
                    else {

                        Toast.makeText(this, "Login effettuato con successo", Toast.LENGTH_SHORT).show();
                    }

                    Intent i = new Intent(this,MyService.class);
                    startService(i);

                    //Passa l'utente all' activity Dashboard
                    Intent intent = new Intent(this,DashBoard.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    this.startActivity(intent);
                    this.finish();

                }else {
                    Toast.makeText(this, "Login fallito", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void annulla(View v){
        user = null;
        setContentView(R.layout.login);
    }

    public void resetCache(){
        String JSONDaily = "{'id':'luiz','age':'25','altezza':'170','sesso':'M','peso':'0','calorie':'0','passi':'0','acqua':'0','bici':'0'}";
        String JSONTips = "{'tip0':'Nessun tip','tip1':'Nessun tip','tip2':'Nessun tip','tip3':'Nessun tip','tip4':'Nessun tip','tip5':'Nessun tip'}";
        String goal = "{'calorie':4000,'acqua':3,'pesoFormaSup':70,'pesoFormaInf':60}";

        MemoryManager memoryManager = new MemoryManager(this);
        memoryManager.writeInMemory("goal",goal);
        memoryManager.writeInMemory("JSONDaily",JSONDaily);
        memoryManager.writeInMemory("JSONTips",JSONTips);
        memoryManager.writeInMemory("penultimopeso","0");
       // memoryManager.writeInMemory("log","false");
    }

    public void startThings(){
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.set(Calendar.HOUR_OF_DAY,0);
        cur_cal.set(Calendar.MINUTE,0);
        System.out.println("Current time " + cur_cal.getTime());
        Intent intent2 = new Intent(this, UserHandler.UserModel.class);
        System.out.println("intent created");
        PendingIntent pi = PendingIntent.getService(this, 0, intent2, 0);
        AlarmManager alarm_manager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,cur_cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);
    }
}

