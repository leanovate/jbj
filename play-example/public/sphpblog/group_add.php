<?php 
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );
	$page_title = _sb('moderation_title');

	// ---------------
	// POST PROCESSING
	// ---------------
	
	require_once('scripts/sb_header.php');
	function page_content() {
	
		$content_template = new Template(TEMPLATE_DIR.'layouts/empty.tpl');
		$form_template = new Template(TEMPLATE_DIR.'admin/group_add.tpl');
		
		// Variables
		$id = '';
		$mode = 'NEW';
		
		// Dropdown menus
		$comment_dropdown = array();
		$comment_dropdown['no_access'] = 			'No Access';
		$comment_dropdown['view'] = 				'+ View';
		$comment_dropdown['post_with_approval'] = 	'+ Post (with Approval)';
		$comment_dropdown['post'] = 				'+ Post (without Approval)';
		$comment_dropdown['moderate'] = 			'+ Moderate';
		$comment_dropdown['edit'] = 				'+ Edit';
		$comment_dropdown['delete'] = 				'+ Delete';
		
		$entry_dropdown = array();
		$entry_dropdown['no_access'] = 			'No Access';
		$entry_dropdown['view'] = 				'+ View';
		$entry_dropdown['post_with_approval'] = '+ Post (with Approval)';
		$entry_dropdown['post'] = 				'+ Post (without Approval)';
		$entry_dropdown['moderate'] = 			'+ Moderate';
		$entry_dropdown['edit'] = 				'+ Edit';
		$entry_dropdown['delete'] = 				'+ Delete';
		
		if (array_key_exists('submit',$_POST) && ($_POST['submit'] == 'Add Group' || $_POST['submit'] == 'Edit Group')) {
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
			$record = new group($id.'.txt');
			$record->read_file(); // Try to read in the existing record or quietly fail
			$record->setTag('ID', $id);
			foreach ($_POST as $key => $val) {
				if (!empty($val) && $key != 'submit' && $key != 'id') { // Ignore empty, submit and id
					switch ($key) {
						case 'superuser':
							$record->setTag('SUPERUSER',	smartstripslashes($_POST['superuser'])=='on'?	'1':'0');
							break;
						case 'comments':
							$record->setTag(strtoupper($key), smartstripslashes($val));
							$record->set_comment_permissions_by_name(smartstripslashes($val));
							break;
						case 'entries':
							$record->setTag(strtoupper($key), smartstripslashes($val));
							$record->set_entry_permissions_by_name(smartstripslashes($val));
							break;
						default:
							$record->setTag(strtoupper($key), smartstripslashes($val));
							break;
					}
				}
			}
			
			// Save record
			$result = $record->write_file();
			
			if ($result !== false) { // Success
				redirect_to_url('group_list.php');
				
				exit;
			} else { // Error
				foreach ($record->data as $key => $val) { // Populate from entered data
					$form_template->setTag('{'.$key.'}', @htmlspecialchars($val, ENT_QUOTES));
				}
				$form_template->setTag('{COMMENT_DROPDOWN}',	html::array_to_dropdown($comment_dropdown, $record->getTag('COMMENTS')));
				$form_template->setTag('{ENTRY_DROPDOWN}',		html::array_to_dropdown($entry_dropdown, $record->getTag('ENTRIES')));
				$form_template->setTag('{SUPERUSER_CHECKED}',	($record->getTag('SUPERUSER')=='1'?		' checked="checked"':''));
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
				$record = new group($id.'.txt');
				if ($record->read_file()) {
					foreach ($record->data as $key => $val) { // Populate from record data
						$form_template->setTag('{'.$key.'}', @htmlspecialchars($val, ENT_QUOTES));
					}
				$form_template->setTag('{COMMENT_DROPDOWN}',	html::array_to_dropdown($comment_dropdown, $record->getTag('COMMENTS')));
				$form_template->setTag('{ENTRY_DROPDOWN}',		html::array_to_dropdown($entry_dropdown, $record->getTag('ENTRIES')));
				$form_template->setTag('{SUPERUSER_CHECKED}',		($record->getTag('SUPERUSER')=='1'?		' checked="checked"':''));
				}
			} else {
				// ----- Default -----
				
				$mode = 'NEW';
				$record = new group(time().'.txt');
				foreach ($record->data as $key => $val) { // Populate from default data
					$form_template->setTag('{'.$key.'}', @htmlspecialchars($val, ENT_QUOTES));
				}
				$form_template->setTag('{COMMENT_DROPDOWN}',	html::array_to_dropdown($comment_dropdown, $record->getTag('COMMENTS')));
				$form_template->setTag('{ENTRY_DROPDOWN}',		html::array_to_dropdown($entry_dropdown, $record->getTag('ENTRIES')));
				$form_template->setTag('{SUPERUSER_CHECKED}',		($record->getTag('SUPERUSER')=='1'?		' checked="checked"':''));
			}
		}
		
		
		// Strings for form
		switch($mode) {
			case 'SUBMIT NEW': // Since we were not redirected then there was an error while saving...
				$form_template->setTag('{FORM_ERROR}', '<p class="error">There was an error saving your data. Please try again.</p>');
			case 'NEW':
				$content_template->setTag('{MAIN}', '<h1>Add Group</h1>');
				$form_template->setTag('{INSTRUCTIONS}', 'Fill out the form below to create a new group.');
				$form_template->setTag('{FORM_ERROR}', '');
				$form_template->setTag('{LEGEND}', 'Add Group');
				$form_template->setTag('{COMMAND}', 'Add Group');
				$form_template->setTag('{ACTION}', BASEURL.basename($_SERVER["SCRIPT_NAME"]));
				break;
			case 'SUBMIT EDIT': // Since we were not redirected then there was an error while saving...
				$form_template->setTag('{FORM_ERROR}', '<p class="error">There was an error saving your data. Please try again.</p>');
			case 'EDIT':
				$content_template->setTag('{MAIN}', '<h1>Edit Group</h1>');
				$form_template->setTag('{INSTRUCTIONS}', 'Fill out the form below to edit the existing group.');
				$form_template->setTag('{FORM_ERROR}', '');
				$form_template->setTag('{LEGEND}', 'Edit Group');
				$form_template->setTag('{COMMAND}', 'Edit Group');
				$form_template->setTag('{ACTION}', BASEURL.basename($_SERVER["SCRIPT_NAME"]));
				break;
		}
	
	
		$content_template->appendTag('{MAIN}', $form_template->getHTML());
		
		
		// PAGE CONTENT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('moderation_title');
		$entry_array[ 'entry' ] = $content_template->getHTML();
		
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
