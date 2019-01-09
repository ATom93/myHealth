<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";

$ID_User =  urldecode($_POST['ID_User']);
$ID_Service = urldecode($_POST['ID_Service']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT ID_User_Service FROM user_service WHERE ID_User='".$ID_User."' AND ID_Service='".$ID_Service."'";
$result = $conn->query($sql);



if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		$ID = $row["ID_User_Service"];
		$ID = (int) $ID;
	}
	
	
	$myJSON = json_encode($ID);
	echo $ID;
    
} else {  
    echo "0 results";
}
$conn->close();
?>