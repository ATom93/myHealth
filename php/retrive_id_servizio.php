<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";

$U_service =  urldecode($_POST['service']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT ID_Service FROM service WHERE Name='".$U_service."'";
$result = $conn->query($sql);



if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		$ID = $row["ID_Service"];
		$ID = (int) $ID;
	}
	
	
	$myJSON = json_encode($ID);
	echo $ID;
    
} else {  
    echo "0 results";
}
$conn->close();
?>