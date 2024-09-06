<?php


require_once dirname(__FILE__) . '/CategoryHandler.php';

$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$category_data = json_decode($data, true);

if (isset($_GET['call'])) {
    switch ($_GET['call']) {

        case 'create':

            if (isset($_POST['name']) && $_FILES['file']['error'] === UPLOAD_ERR_OK) {
                $db = new CategoryHandler();

                $file = $_FILES['file']['tmp_name'];

                $name = $_POST['name'];
                $parent_cat_id = $_POST['parent_cat_id'];
                $type = $_POST['type'];

                $fileExt = getFileExtension($_FILES['file']['name']);

                $category_data = array("name" => $name, "parent_cat_id" => $parent_cat_id, "type" => $type);
                $result = $db->createCategoryWithFile($file, $fileExt, $category_data);


                if ($result != null && $result != "error") {
                    $response = $result;
                }
            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'getAllCategory':

            $db = new CategoryHandler();
            $result = $db->getAllCategory();

            $response = $result;

            break;

        case 'getMainCategory':

            $db = new CategoryHandler();
            $result = $db->getAllMainCategory();

            $response = $result;

            break;

        case 'getParentCategory':

            if (isset($_POST['sub_cat_id'])) {
                $sub_cat_id = $_POST['sub_cat_id'];
                $db = new CategoryHandler();
                $result = $db->getAllParentCategory($sub_cat_id);

                if ($result != null && $result != "error") {
                    $response = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'getSubCategory':

            if (isset($_POST['parent_cat_id'])) {
                $parent_cat_id = $_POST['parent_cat_id'];
                $db = new CategoryHandler();
                $result = $db->getAllSubCategoryById($parent_cat_id);

                if ($result != null && $result != "error") {
                    $response = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'getSingleCategory':

            if (isset($_POST['id'])) {
                $id = $_POST['id'];
                $db = new CategoryHandler();
                $result = $db->getCategoryById($id);

                if ($result != null && $result != "error") {
                    $response = $result;
                }

            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

        case 'getAllSubCategory':

            $db = new CategoryHandler();
            $result = $db->getAllSubCategory();

            $response = $result;

            break;

        case 'update':

            if (isset($_POST['id']) && isset($_POST['name'])) {

                $db = new CategoryHandler();

                $id = $_POST['id'];
                $name = $_POST['name'];
                $parent_cat_id = $_POST['parent_cat_id'];
                $type = $_POST['type'];
                $image_url = $_POST['image_url'];

                $category_data = array("id" => $id, "name" => $name, "parent_cat_id" => $parent_cat_id, "type" => $type, "image_url" => $image_url);
                $result = $db->updateCategory($category_data);

                if ($result != null && $result != "error") {
                    $response = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Error, Please try again!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'updateWithImage':

            if (isset($_POST['id']) && isset($_POST['name']) && $_FILES['file']['error'] === UPLOAD_ERR_OK) {

                $db = new CategoryHandler();

                $file = $_FILES['file']['tmp_name'];

                $id = $_POST['id'];
                $name = $_POST['name'];
                $parent_cat_id = $_POST['parent_cat_id'];
                $type = $_POST['type'];
                $image_url = $_POST['image_url'];

                $fileExt = getFileExtension($_FILES['file']['name']);

                $category_data = array("id" => $id, "name" => $name, "parent_cat_id" => $parent_cat_id, "type" => $type, "image_url" => $image_url);
                $result = $db->updateCategoryWithFile($file, $fileExt, $category_data);


                if ($result != null && $result != "error") {
                    $response = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Error, Please try again!';
                }


            } else if (isset($_POST['id']) && isset($_POST['name'])) {

                $db = new CategoryHandler();

                $id = $_POST['id'];
                $name = $_POST['name'];
                $parent_cat_id = $_POST['parent_cat_id'];
                $type = $_POST['type'];
                $image_url = $_POST['image_url'];

                $category_data = array("id" => $id, "name" => $name, "parent_cat_id" => $parent_cat_id, "type" => $type, "image_url" => $image_url);
                $result = $db->updateCategory($category_data);

                if ($result != null && $result != "error") {
                    $response = $result;
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Error, Please try again!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Required fields are missing';
            }

            break;

        case 'delete':

            if (isset($_POST['id']) && isset($_POST['image_url'])) {
                $id = $_POST['id'];
                $image_url = $_POST['image_url'];
                $db = new CategoryHandler();
                $result = $db->deleteCategory($id, $image_url);

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