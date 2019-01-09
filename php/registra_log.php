<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_username = urldecode($_POST['username']);
$movement = urldecode($_POST['movement']);
$cal = urldecode($_POST['cal']);
$water = urldecode($_POST['water']);
$date = urldecode($_POST['date']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT ID_User FROM user WHERE Username='".$U_username."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    
    while($row = $result->fetch_assoc()) {
    
    	$ID_User = $row['ID_User'];
		
        $sql1 = "INSERT INTO `my_adaptapppoliba`.`model` (`ID_Model`, `ID_User`, `Movement`, `Cal`, `Water`, `Date`) VALUES (NULL, '".$ID_User."', '".$movement."', '".$cal."', '".$water."', '".$date."');";

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
