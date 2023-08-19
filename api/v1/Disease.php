<?php


require_once dirname(__FILE__) . '/DiseaseHandler.php';
require_once dirname(__FILE__) . '/TokenHandler.php';

$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$disease_data = json_decode($data, true);

if (isset($_GET['call'])) {
    switch ($_GET['call']) {


        case 'create':

            if (isset($_POST['name']) && isset($_POST['cat_id'])) {
                $db = new DiseaseHandler();

                $name = $_POST['name'];
                $cat_id = $_POST['cat_id'];
                $clue_to_dx = $_POST['clue_to_dx'];
                $advice = $_POST['advice'];
                $treatment = $_POST['treatment'];

                $disease_data = array("name" => $name, "cat_id" => $cat_id, "clue_to_dx" => $clue_to_dx, "advice" => $advice, "treatment" => $treatment);

                $result = $db->createDisease($disease_data);

                if ($result != null && $result != "error") {
                    $response['error'] = true;
                    $response['response'] = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'createDiseaseImage':

            if (isset($_POST['disease_id']) && $_FILES['file']['error'] === UPLOAD_ERR_OK) {
                $db = new DiseaseHandler();

                $disease_id = $_POST['disease_id'];

                $file = $_FILES['file']['tmp_name'];
                $fileExt = getFileExtension($_FILES['file']['name']);

                $result = $db->createDiseaseImage($disease_id, $file, $fileExt);

                if ($result != null && $result != "error") {
                    $response['error'] = true;
                    $response['response'] = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'createArticle':

            if (isset($_POST['name']) && isset($_POST['details'])) {
                $db = new DiseaseHandler();

                $name = $_POST['name'];
                $cat_id = $_POST['cat_id'];
                $details = $_POST['details'];

                $disease_data = array("name" => $name, "cat_id" => $cat_id, "details" => $details);

                $result = $db->createArticle($disease_data);

                if ($result != null && $result != "error") {
                    $response['error'] = true;
                    $response['response'] = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'getDiseaseDetails':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if (isset($_POST['id'])) {
                        $id = $_POST['id'];
                        $db = new DiseaseHandler();
                        $result = $db->getDiseaseDetails($id);

                        if ($result != null && $result != "error") {

                            $disease_images = $db->getAllDiseaseImage($result["id"]);
                            $result["disease_image"] = $disease_images;
                            $response = $result;
                        }

                    } else {
                        $response['error'] = true;
                        $response['response'] = 'Error, Please Try Again!';
                    }
                } else {
                    http_response_code(401);
                }
            } else {
                http_response_code(401);
            }

            break;

        case 'getDiseasesByCat':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    if (isset($_POST['cat_id'])) {
                        $cat_id = $_POST['cat_id'];
                        $db = new DiseaseHandler();
                        $result = $db->getDiseasesByCat($cat_id);

                        if ($result != null && $result != "error") {

                            foreach ($result as $item) {
                                $disease_images = $db->getAllDiseaseImage($item["id"]);
                                $item["disease_image"] = $disease_images;
                                array_push($response, $item);
                            }
                            //$response = $result;
                        }

                    } else {
                        $response['error'] = true;
                        $response['response'] = 'Error, Please Try Again!';
                    }
                } else {
                    http_response_code(401);
                }
            } else {
                http_response_code(401);
            }

            break;

        case 'getAllDiseases':
            $headers = getallheaders();
            $jwtToken = isset($headers["Authorization"]) ? $headers["Authorization"] : null;
            //$jwtToken=$_SERVER['HTTP_AUTHORIZATION'];
            if ($jwtToken) {
                $tokenHandler = new TokenHandler();
                $isTokenValid = $tokenHandler->validateJWT($jwtToken);

                if ($isTokenValid) {
                    $db = new DiseaseHandler();
                    $result = $db->getAllDisease();
                    foreach ($result as $item) {
                        $disease_images = $db->getAllDiseaseImage($item["id"]);
                        $item["disease_image"] = $disease_images;
                        array_push($response, $item);
                    }

                    //$response = $result;
                } else {
                    http_response_code(401);
                }
            } else {
                http_response_code(401);
            }

            break;

        case 'getAllDiseaseImages':

            $db = new DiseaseHandler();
            $result = $db->getAllDiseaseImage(1);

            $response = $result;

            break;

        case 'update':

            if (isset($_POST['id']) && isset($_POST['name']) && isset($_POST['cat_id'])) {
                $db = new DiseaseHandler();

                $id = $_POST['id'];
                $name = $_POST['name'];
                $cat_id = $_POST['cat_id'];
                $clue_to_dx = $_POST['clue_to_dx'];
                $advice = $_POST['advice'];
                $treatment = $_POST['treatment'];

                $disease_data = array("id" => $id, "name" => $name, "cat_id" => $cat_id, "clue_to_dx" => $clue_to_dx, "advice" => $advice, "treatment" => $treatment);

                $result = $db->updateDisease($disease_data);

                if ($result != null && $result != "error") {
                    $response = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'updateArticle':

            if (isset($_POST['id']) && isset($_POST['name']) && isset($_POST['cat_id'])) {
                $db = new DiseaseHandler();

                $id = $_POST['id'];
                $name = $_POST['name'];
                $cat_id = $_POST['cat_id'];
                $details = $_POST['details'];

                $disease_data = array("id" => $id, "name" => $name, "cat_id" => $cat_id, "details" => $details);

                $result = $db->updateArticle($disease_data);

                if ($result != null && $result != "error") {
                    $response = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'delete':

            if (isset($_POST['id'])) {
                $id = $_POST['id'];
                $db = new DiseaseHandler();
                $result = $db->deleteDisease($id);

                if ($result == "deleted") {
                    $response['error'] = false;
                    $response['response'] = 'Deleted Successfully!';
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'deleteDiseaseImage':

            if (isset($_POST['id'])) {
                $id = $_POST['id'];
                $db = new DiseaseHandler();
                $result = $db->deleteDiseaseImage($id);

                if ($result == "deleted") {
                    $response['error'] = false;
                    $response['response'] = 'Deleted Successfully!';
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

    }
}

function getFileExtension($file)
{
    $path_parts = pathinfo($file);
    return $path_parts['extension'];
}

echo json_encode($response);

?>
