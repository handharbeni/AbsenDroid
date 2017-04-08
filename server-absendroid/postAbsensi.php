<?php 
	include "connection.php";
	$params = $_POST;
	if(isset($_POST)){
		$mysqli = new mysqli($db_host, $db_user, $db_pass, $db_name);
		if ($mysqli->connect_errno) {
		    printf("Connect failed: %s\n", $mysqli->connect_error);
		    exit();
		}
		$perusahaan_code 	=	$params['perusahaan_id'];
		$random_code		=	$params['random_code'];
		$state_code			=	$params['qr_state'];

		$auth_code			=	"U".$perusahaan_code."#".$random_code."#".$state_code;
		$query_auth			=	"SELECT * FROM qr_store WHERE qr_code = '".$auth_code."'";
		$select_auth		=	$mysqli->query($query_auth);
		if($select_auth){
			// berhasil ambil data
			// if($select_auth->num_rows > 0){
				// data valid
				// insert data presensi
				// perusahaan_id
				// pegawai_id
				// waktu_android
				// waktu
				// qr_state
				// imei
				// latitude
				// longitude
				// akurasi
				$perusahaan_id		=	$params['perusahaan_id'];
				$pegawai_id			=	$params['pegawai_id'];
				$waktu_android		=	$params['waktu_android'];
				$waktu 				=	strftime("%F %T");
				$qr_state			=	$params['qr_state'];
				$imei				=	$params['imei'];
				$latitude			=	$params['latitude'];
				$longitude			=	$params['longitude'];
				$akurasi			=	$params['akurasi'];				
				$tanggal			=	explode(" ", $params['waktu_android']);


				$query_presensi		=	"INSERT INTO presensi (perusahaan_id, pegawai_id, waktu_android, waktu, qr_state, imei, latitude, longitude, akurasi, tanggal) VALUES ('$perusahaan_id', '$pegawai_id', '$waktu_android', '$waktu', '$qr_state', '$imei', '$latitude', '$longitude', '$akurasi', '$tanggal[0]')";
				$insert_data = $mysqli->query($query_presensi);
				if($insert_data){
					// berhasil insert data
					echo json_encode(array("status"=>4));
				}else{
					// gagal insert data
					echo json_encode(array("status"=>3));
				}
			// }else{
				// data tidak valid
				// kembalikan 2
				// echo json_encode(array("status"=>2));
			// }
		}else{
			// gagal mengmbil data
			// kembalikan 1
			echo json_encode(array("status"=>1));
		}
	}else{
		// tidak ada masukan
		// kembalikan 0
		echo json_encode(array("status"=>0));
	}
?>