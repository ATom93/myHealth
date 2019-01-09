<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_username = urldecode($_POST['username']);
$service = urldecode($_POST['app']);
$value = urldecode($_POST['value']);
$date = urldecode($_POST['date']);
$time = urldecode($_POST['time']);


// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT US.ID_User_Service
FROM user as U inner join user_service as US on U.ID_User=US.ID_User
inner join service as S on US.ID_Service=S.ID_Service
WHERE U.Username='".$U_username."' and S.Name='".$service."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    
    while($row = $result->fetch_assoc()) {
		
         $ID_User_Service = $row["ID_User_Service"];
         
         $sql1 = "INSERT INTO `my_adaptapppoliba`.`usage` (`ID_Usage`, `ID_User_Service`, `Value`, `Date`, `Time`) VALUES (NULL, '".$ID_User_Service."', '".$value."', '".$date."','".$time."');";

            if (mysqli_query($conn, $sql1)) {
                 echo "2"; //Utente loggato con successo
            } else {
                 echo "4"; //Login fallito    
            }  
       
    }
      
} else {
    echo "6"; //Username non trovato
}

$conn->close();
?>