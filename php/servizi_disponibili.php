<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$ID = urldecode($_POST['ID']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT *
FROM service
WHERE ID_Service != ALL(SELECT ID_Service
FROM user_service
WHERE ID_User = '".$ID."')";
$result = $conn->query($sql);

$i=0;



if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        	
		$obj->Name["Name".$i] = $row["Name"];
		$obj->Description["Description".$i] = $row["Description"];             
        
		$i=$i+1;     
	}
	
	
	$myJSON = json_encode($obj);
	echo "servizi".$myJSON;
    
} else {  
    echo "0 results";
}
$conn->close();
?>
