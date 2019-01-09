<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$ID = urldecode($_POST['ID']);
$U_servizio = urldecode($_POST['servizio']);



// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT ID_User,Password FROM user WHERE Username='".$U_username."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    
    while($row = $result->fetch_assoc()) {
		
         $U_userID = $row["ID_User"];
         
         if($U_password == $row["Password"]){
	
             $sql1 = "INSERT INTO log(ID_User,Username) VALUES ('".$U_userID."','".$U_username."')";

            if (mysqli_query($conn, $sql1)) {
                 echo "2"; //Utente loggato con successo
            } else {
                 echo "3"; //Login fallito    
            }     
         
        }
        else
        {
        		echo "3"; //Password errata
        }
    }
      
} else {
    echo "3"; //Username non trovato
}

$conn->close();
?>
