<?php
include "connection.php";
$params = $_POST;
if(!empty($params['imei'])){
	$mysqli = new mysqli($db_host, $db_user, $db_pass, $db_name);
	if ($mysqli->connect_errno) {
	    printf("Connect failed: %s\n", $mysqli->connect_error);
	    exit();
	}
	if ($result = $mysqli->query("SELECT * FROM pegawai WHERE imei = '".$params['imei']."'")) {
		if($result->num_rows > 0){
			$row = mysqli_fetch_assoc($result);
			$return = array(
					'status' 			=>	2,
					'id'				=>	$row['id'],
					'id_perusahaan'		=>	$row['perusahaan_id'],
					'nama'				=>	$row['nama'],
					'email'				=>	$row['email'],
					'key'				=>	$row['imei']
				);
		    $result->close();
		    echo json_encode($return);
		}else{
			$result->close();
			echo json_encode(array("status"=>1));
		}
	}
	$mysqli->close();	
}else{
	echo json_encode(array("status"=>0));
}
?>