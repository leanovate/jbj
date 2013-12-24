<?php 
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('Add User');
	require_once('scripts/sb_header.php');

	// ---------------
	// POST PROCESSING
	// ---------------
	
	function page_content() {
	
		$content_template = new Template(TEMPLATE_DIR.'layouts/empty.tpl');
		$form_template = new Template(TEMPLATE_DIR.'admin/user_add.tpl');
		
		// Variables
		$id = '';
			
		$mode = 'NEW';
		
		if (array_key_exists('submit',$_POST) && ($_POST['submit'] == 'Add User' || $_POST['submit'] == 'Edit User')) {
			// ----- New / Update -----
		
			if (array_key_exists('id',$_POST)) {
				if (preg_match('/^(\d{10})/', smartstripslashes($_POST['id']), $matches)) {
					$mode = 'SUBMIT EDIT';
					$id = $matches[0]; // Update record
				}
			}
			
			if (empty($id)) {
				$mode = 'SUBMIT NEW';
				$id = time(); // New record
			}
			
			// Save data
			$record = new User($id.'.txt');
			$record->read_file(); // Try to read in the existing record or quietly fail
			$record->setTag('ID', $id);
			foreach ($_POST as $key => $val) {
				if (!empty($val) && $key != 'submit' && $key != 'id') { // Ignore empty, submit and id
					if ($key == 'password') { // Special case
						$old_password_hash = $record->getTag('PASSWORD');
						$new_password = smartstripslashes($val);
						if ($new_password === $old_password_hash) {
							// Ignore (It's the same password hash)
						} else {
							$record->setTag(strtoupper($key), crypt($new_password));
						}
						
						/*
						// We don't need to do this, but if you want to check if it's the same...
						if (crypt($new_password, $old_password_hash)===$old_password_hash) {
							 echo('same');
						} else {
							 echo('different');
						}
						*/
					} else {
						$record->setTag(strtoupper($key), smartstripslashes($val));
					}
				}
			}
			// Checkboxes are a special case
			$record->setTag('ACTIVE', smartstripslashes($_POST['active'])=='on'? '1':'0');
			
			// Save record
			$result = $record->write_file();
			
			if ($result !== false) { // Success
				redirect_to_url('user_list.php');
				
				exit;
			} else { // Error
				foreach ($record->data as $key => $val) { // Populate from entered data
					$form_template->setTag('{'.$key.'}', @htmlspecialchars($val, ENT_QUOTES));
				}
				$form_template->setTag('{ACTIVE_CHECKED}', ($record->getTag('ACTIVE')=='1'?' checked="checked"':''));
				$form_template->setTag('{GROUP_DROPDOWN}', groups_to_drop_down($record->getTag('GROUP')));
			}
			
		} else {
			// ----- Edit / Default -----
		
			if (array_key_exists('id',$_GET)) {
				if (preg_match('/^(\d{10})/', smartstripslashes(urldecode($_GET['id'])), $matches)) {
					$id = $matches[0]; // Editing
				}
			}
			
			if (!empty($id) && array_key_exists('command',$_GET) && $_GET['command'] == 'edit') {
				// ----- Edit -----
				
				$mode = 'EDIT';
				$record = new User($id.'.txt');
				if ($record->read_file()) {
					foreach ($record->data as $key => $val) { // Populate from record data
						$form_template->setTag('{'.$key.'}', @htmlspecialchars($val, ENT_QUOTES));
					}
					$form_template->setTag('{ACTIVE_CHECKED}', ($record->getTag('ACTIVE')=='1'?' checked="checked"':''));
					$form_template->setTag('{GROUP_DROPDOWN}', groups_to_drop_down($record->getTag('GROUP')));
				}
			} else {
				// ----- Default -----
				
				$mode = 'NEW';
				$record = new User(time().'.txt');
				foreach ($record->data as $key => $val) { // Populate from default data
					$form_template->setTag('{'.$key.'}', @htmlspecialchars($val, ENT_QUOTES));
				}
				$form_template->setTag('{ACTIVE_CHECKED}', ($record->getTag('ACTIVE')=='1'?' checked="checked"':''));
				$form_template->setTag('{GROUP_DROPDOWN}', groups_to_drop_down($record->getTag('GROUP')));
			}
		}
		
		
		// Strings for form
		switch($mode) {
			case 'SUBMIT NEW': // Since we were not redirected then there was an error while saving...
				$form_template->setTag('{FORM_ERROR}', '<p class="error">There was an error saving your data. Please try again.</p>');
			case 'NEW':
				$content_template->setTag('{MAIN}', '<h1>Add User</h1>');
				$form_template->setTag('{INSTRUCTIONS}', 'Fill out the form below to create a new user.');
				$form_template->setTag('{FORM_ERROR}', '');
				$form_template->setTag('{LEGEND}', 'Add User');
				$form_template->setTag('{COMMAND}', 'Add User');
				$form_template->setTag('{ACTION}', BASEURL.basename($_SERVER["SCRIPT_NAME"]));
				break;
			case 'SUBMIT EDIT': // Since we were not redirected then there was an error while saving...
				$form_template->setTag('{FORM_ERROR}', '<p class="error">There was an error saving your data. Please try again.</p>');
			case 'EDIT':
				$content_template->setTag('{MAIN}', '<h1>Edit User</h1>');
				$form_template->setTag('{INSTRUCTIONS}', 'Fill out the form below to edit the existing user.');
				$form_template->setTag('{FORM_ERROR}', '');
				$form_template->setTag('{LEGEND}', 'Edit User');
				$form_template->setTag('{COMMAND}', 'Edit User');
				$form_template->setTag('{ACTION}', BASEURL.basename($_SERVER["SCRIPT_NAME"]));
				break;
		}
	
	
		$content_template->appendTag('{MAIN}', $form_template->getHTML());
		
		
		// PAGE CONTENT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('Add User');
		$entry_array[ 'entry' ] = $content_template->getHTML();
		
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
