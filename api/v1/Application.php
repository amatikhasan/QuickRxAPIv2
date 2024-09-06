<?php


require_once dirname(__FILE__) . '/ApplicationHandler.php';

$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$info_data = json_decode($data, true);

if (isset($_GET['create'])) {
    switch ($_GET['create']) {

        case 'feedback':

            if (isset($_POST['user_id']) && isset($_POST['feedback'])) {
                $user_id = $_POST['user_id'];
                $feedback = $_POST['feedback'];

                $save = new ApplicationHandler();
                $result = $save->createFeedback($user_id, $feedback);


                if ($result == "ok") {
                    $response['error'] = false;
                    $response['response'] = 'Feedback Recieved!';
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Error, Please Try Again!';
                }
            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;
    }
}

if (isset($_GET['get'])) {
    switch ($_GET['get']) {

        case 'info':

            $db = new ApplicationHandler();
            $result = $db->getAppInfo();

            $response = $result;

            break;

        case 'feedback':

            $db = new ApplicationHandler();
            $result = $db->getFeedback();

            $response = $result;

            break;

    }
}

if (isset($_GET['update'])) {
    switch ($_GET['update']) {

        case 'info':

            if (isset($_POST['id'])) {
                $subscription_fee = $_POST['subscription_fee'];
                $account_number = $_POST['account_number'];
                $hotline = $_POST['hotline'];
                $facebook_link = $_POST['facebook_link'];
                $about_us = $_POST['about_us'];
                $terms = $_POST['terms_and_condition'];

                $save = new ApplicationHandler();
                $result = $save->updateAppInfo($id,$subscription_fee,$account_number, $hotline, $facebook_link, $about_us, $terms);


                if ($result == "updated") {
                    $response['error'] = false;
                    $response['response'] = 'Updated Successfully!';
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Nothing Changed!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

    }

}

echo json_encode($response);


?>