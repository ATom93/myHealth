<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_username = urldecode($_POST['username']);
$service = urldecode($_POST['service']);



// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT USG.Value
FROM user as U inner join user_service as US on U.ID_User=US.ID_User inner join service as S on US.ID_Service = S.ID_Service inner join `usage` as USG on USG.ID_User_Service=US.ID_User_Service
WHERE U.Username='".$U_username."' and S.Name='".$service."'";
$result = $conn->query($sql);
$i=0;

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        	
		$obj->Value["Value".$i] = $row["Value"];           
		$i=$i+1;     
	}
	
	
	$myJSON = json_encode($obj);
	echo "valori".$myJSON;
    
} else {  
    echo "valori{\"Value\":{\"Value0\":\"0\",\"Value1\":\"0\",\"Value2\":\"0\",\"Value3\":\"0\",\"Value4\":\"0\",\"Value5\":\"0\"}}";
}
$conn->close();
?>
