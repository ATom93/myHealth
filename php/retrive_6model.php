<?php
$servername = "localhost";
$username = "adaptapppoliba";
$password = "";
$dbname = "my_adaptapppoliba";


$U_username = urldecode($_POST['username']);


// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT U.Username,M.ID_Model,M.Movement,M.Cal,M.Water,M.Date
FROM `model` as M inner join user as U on M.ID_User=U.ID_User
WHERE U.Username='".$U_username."'
ORDER BY M.Date DESC
LIMIT 6";

$result = $conn->query($sql);
$i=0;

$myJSON = "{";

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        	
		$myObj->movement = $row['Movement'];
        $myObj->cal = $row['Cal'];
        $myObj->water = $row['Water'];
        $tempJSON = json_encode($myObj);
        if($i==0){
        	$myJSON = $myJSON."\"model".$i."\":".$tempJSON;
        }
        else{
        	$myJSON = $myJSON.",\"model".$i."\":".$tempJSON;
        }
        
        $i = $i + 1;
	}
    
    $myJSON = $myJSON."}";
    
	//$finalJSON = json_encode($finalObj);
	echo $myJSON;
} else {  
    echo "0 results";
}
$conn->close();
?>
