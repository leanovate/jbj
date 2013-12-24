<?php
	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com

	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title =_sb('manage_users_title');

	// --------------
	// PRE-PROCESSING
	// --------------

	
	// ---------- CANCEL ----------
	if ( array_key_exists( 'cancel', $_POST ) ) {
		redirect_to_url('manage_users.php');
	}
	
	
	// ---------- PERMISSIONS ----------
	$admin = $_SESSION[ 'fulladmin' ];
	if ( $admin == 'no' ) { redirect_to_url('index.php'); }
	

	// ---------- UPDATE USER ----------
	if ( $_GET[ 'action' ] == "update" ) {
		// Since we arent adding a new user - we can blindly update the record as required
		if ($_SESSION[ 'fulladmin' ] != 'yes' ) {
			echo(_sb('fulladminerror'));
		} else {

	                $user_list = read_users();
        	        $new_user_list = array();
                	foreach ($user_list as $user) {
                        	if ($_GET[ 'type' ] == "edit") {
					if ($user[1] != $_GET[ 'user' ]) {
                                		$new_user_list[] = $user;
					}
                       		} else {
                                	$new_user_list[] = $user;
				}
	                }

			// Add our new record onto the end of the structure

			// Manufacture the active state properly
			if ( $_POST[ 'sActive' ] == 'on' ) {
				$active = "Y";
			} else { $active = "N"; }

			if ( $_POST[ 'sModComments' ] == 'on' ) {
				$modcomments = "Y";
			} else { $modcomments = "N"; }

			if ( $_POST[ 'sDeleteEntries' ] == 'on' ) {
				$deleteentries = "Y";
			} else { $deleteentries = "N"; }

			if ( $_POST[ 'sEditAny' ] == 'on' ) {
				$editany = "Y";
			} else { $editany = "N"; }

			$blankfield = "";
			
			// Create the record structure
			if ( $_GET[ 'type' ] == "edit" ) {
				// Now deal with the passwords - since we dont have the decrypt possibility
				// we have to compare as required (if we blind post - it will encrypt the crypted pass ;-)
				$password = $_GET[ 'oldpasshash' ];
				if ( $password != $_POST[ 'sPassword' ] ) {
					$password = crypt( $_GET[ 'user' ], $_POST[ 'sPassword' ] );
				}

				$array = array($_POST[ 'sFullname' ], $_GET[ 'user' ], $password, $_POST[ 'sAvatar' ], $active, $_POST[ 'sEmail' ], $modcomments, $deleteentries, $editany, $blankfield);
			} else {
				$array = array($_POST[ 'sFullname' ], $_POST[ 'sUsername' ], crypt( $_POST[ 'sUsername' ], $_POST[ 'sPassword' ] ), $_POST[ 'sAvatar' ], $active, $_POST[ 'sEmail' ], $modcomments, $deleteentries, $editany, $blankfield);
			}
			$new_user_list[] = $array;
			write_users($new_user_list);

			redirect_to_url("manage_users.php");
		}
	}
	// ---------- UPDATE USER ----------
	

	// ---------- DELETE USER ----------
	// Basically read the whole file leaving out the user's line we're deleting
	if ( ($_SESSION[ 'fulladmin' ] == 'yes' ) AND ( $_GET[ 'action' ] == "delete" )) {
		$user_list = read_users();
		$new_user_list = array();
		foreach ($user_list as $user) {
			if ($user[1] != $_GET[ 'user' ]) {
				$new_user_list[] = $user;
			}
		}
		write_users($new_user_list);
	
		redirect_to_url("manage_users.php");
	}
	// ---------- DELETE USER ----------
	

	// -----------
	// PAGE CONTENT
	// -----------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $theme_vars, $blog_theme;

		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('manage_users_title');

		// PAGE CONTENT BEGIN
		ob_start();

		// PAGE BODY

		// MODIFY or CREATE NEW USER
		// Display a form for editing the user...this does not actually post the changes
		if ( $_GET[ 'action' ] == "modify" ) {

			// Only do this if we are editing - otherwise leave the fields blank
			if ( $_GET[ 'type' ] == "edit" ) {
				// Now get the information about this user
				$user_list = read_users();
				foreach($user_list as $tmp) {
					if ( $tmp[1] == $_GET[ 'user' ] ) {
						Break;
					}
				}
			}

			// Show it to the user in an editable fashion
			if ( $_GET[ 'type' ] == "edit" ) {
				echo('<form action="manage_users.php?action=update&type=edit&oldpasshash=' . $tmp[2] . '&user=' . $tmp[1] . '" method="post" name="manage_users" id="manage_users">');
			} else {
				echo('<form action="manage_users.php?action=update&type=new" method="post" name="manage_users" id="manage_users">');
			}
			
			// $GLOBALS['theme_vars']['content_width']
			// $GLOBALS['theme_vars']['max_image_width']
			
			echo( '<table width="'.$GLOBALS['theme_vars']['content_width'].'" cellspacing="0" cellpadding="2">' );
			if ( $_GET[ 'type' ] == "edit" ) {
			echo( '<tr class="header"><th></th><th>' . _sb('header_user') . $tmp[1] . '</th><th></th></tr>'); }
			echo( '<tr class="header"><th></th><th>' . _sb('header_property') . '</th><th>' . _sb('header_value') . '</th></tr>');

			if ( $_GET[ 'type' ] == "new" ) {
				// Populate default values
				$tmp[4] = 'Y'; // Active
				$tmp[6] = 'Y'; // Allow moderation

				echo( '<tr class="data2"><td></td><td>' . _sb('prop_username') . '</td><td><input type="text" name="sUsername" value="" autocomplete="OFF" size="40";"></td></tr>');
			}
			echo( '<tr class="data1"><td></td><td>' . _sb('prop_fullname') . '</td><td><input type="text" name="sFullname" value="' . htmlspecialchars($tmp[0]) . '" autocomplete="OFF" size="40";"></td></tr>');
			echo( '<tr class="data2"><td></td><td>' . _sb('prop_password') . '</td><td><input type="password" name="sPassword" value="' . htmlspecialchars($tmp[2]) . '" autocomplete="OFF" size="60";"></td></tr>');
			echo( '<tr class="data1"><td></td><td>' . _sb('prop_email') . '</td><td><input type="text" name="sEmail" value="' . htmlspecialchars($tmp[5]) . '" autocomplete="OFF" size="40";"></td></tr>');
			echo( '<tr class="data2"><td></td><td>' . _sb('prop_avatar') . '</td><td><input type="text" name="sAvatar" value="' . htmlspecialchars($tmp[3]) . '" autocomplete="OFF" size="60";"></td></tr>');
			echo( '<tr class="data1"><td></td><td>' . _sb('prop_state') . '</td><td><input type="checkbox" name="sActive"');
			if ( $tmp[4] == 'Y' ) { echo ( ' checked'); } else { echo ( ' unchecked'); };
			echo( '></td></tr>');
			echo( '<tr class="data2"><td></td><td>' . _sb('prop_sec_Moderate') . '</td><td><input type="checkbox" name="sModComments"');
			if ( $tmp[6] == 'Y' ) { echo ( ' checked'); } else { echo ( ' unchecked'); };
			echo( '></td></tr>');
			echo( '<tr class="data1"><td></td><td>' . _sb('prop_sec_Delete') . '</td><td><input type="checkbox" name="sDeleteEntries"');
			if ( $tmp[7] == 'Y' ) { echo ( ' checked'); } else { echo ( ' unchecked'); };
			echo( '></td></tr>');
			echo( '<tr class="data2"><td></td><td>' .	 _sb('prop_sec_Edit') . '</td><td><input type="checkbox" name="sEditAny"');
			if ( $tmp[8] == 'Y' ) { echo ( ' checked'); } else { echo ( ' unchecked'); };
			echo( '></td></tr>');

			echo('</table>');
			echo('<hr />');
			if ( $_GET[ 'type' ] == "edit" ) {
				echo('<input type="submit" name="submit" value="' . _sb('btn_SaveChanges') . '" />');
			} else {
				echo('<input type="submit" name="submit" value="' . _sb('btn_CreateUser') . '" />');
			}
			echo('<input type="submit" name="cancel" value="' . _sb('btn_Cancel') . '" />');
			echo('</form>');
		}

		// USER GRID
		// 1. Only show complete list to Administrator
		// 2. Only show list when not modifying, adding or deleting an entry
		if ( ($_SESSION[ 'fulladmin' ] == 'yes' ) AND ( $_GET[ 'action' ] == "" ))	{
			// Button to add new user - only available to Administrator
			echo( '<a href="manage_users.php?action=modify&type=new">' . _sb('create_user') . '</a><p />');
			echo( '<table width="'.$GLOBALS['theme_vars']['content_width'].'" cellspacing="0" cellpadding="2">' );
			echo( '<tr class="header"><th></th><th><i>' . _sb('grid_header') . '</i></th><th></th><th></th><th></th></tr>');
			echo( '<tr class="header"><th></th><th>' . _sb('grid_login') . '</th><th>' . _sb('grid_email') . '</th><th>' . _sb('grid_avatar') . '</th><th>' . _sb('grid_state') . '</th></tr>');

			$user_list = read_users();

			// sort user listing by login, array index 1
			$sort_col = array();
			foreach ($user_list as $sub) $sort_col[] = $sub[1];
			array_multisort($sort_col, $user_list);

			foreach ($user_list as $tmp) {
					if (($linecount % 2) == 0) {
						echo( '<tr class="data1"><td></td><td><b>' . $tmp[0] . '</b></td><td></td><td><b><a href="manage_users.php?action=modify&type=edit&user=' . $tmp[1] . '">' . _sb('btn_modify') . '</a>&nbsp|&nbsp<a href="manage_users.php?action=delete&user=' . $tmp[1] . '">' . _sb('btn_delete') . '</a></b></td><td></td></tr>');
						echo( '<tr class="data1"><td></td><td>' . $tmp[1] . '</td><td>' . $tmp[5] . '</td><td>' . $tmp[3] . '</td><td>' . $tmp[4] . '</td></tr>');
					} else {
						echo( '<tr class="data2"><td></td><td><b>' . $tmp[0] . '</b></td><td></td><td><b><a href="manage_users.php?action=modify&type=edit&user=' . $tmp[1] . '">' . _sb('btn_modify') . '</a>&nbsp|&nbsp<a href="manage_users.php?action=delete&user=' . $tmp[1] . '">' . _sb('btn_delete') . '</a></b></td><td></td></tr>');
						echo( '<tr class="data2"><td></td><td>' . $tmp[1] . '</td><td>' . $tmp[5] . '</td><td>' . $tmp[3] . '</td><td>' . $tmp[4] . '</td></tr>');
					}
					$linecount = $linecount + 1;
			}
			echo( '</table>' );
			// END USER GRID
		}

		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();

		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
