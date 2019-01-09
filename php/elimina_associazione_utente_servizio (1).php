<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$ID_utente = urldecode($_POST['ID_utente']);
$service = urldecode($_POST['service']);



// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT ID_Service FROM service WHERE Name='".$service."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    
    while($row = $result->fetch_assoc()) {
		
		$ID_service = $row["ID_Service"];
		
		$sql1 = "DELETE FROM user_service WHERE ID_User='".$ID_utente."' AND ID_Service='".$ID_service."'";
		
		if (mysqli_query($conn, $sql1)) {
                 
			echo "2"; //Usage creato con successo
            
		} else {
        
			echo "3"; //Login fallito    
		} 
    
        
    }
      
} else {
    echo "4"; //Username non trovato
}

$conn->close();
?>
