<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";

$U_username =  urldecode($_POST['username']);

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT Sex FROM user WHERE Username='".$U_username."'";
$result = $conn->query($sql);



if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		
		$sesso = $row["Sex"];
	}
	
	
	$myJSON = json_encode($sesso);
	echo $sesso;
    
} else {  
    echo "0 results";
}
$conn->close();
?>
