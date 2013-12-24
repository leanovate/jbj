<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );
	$page_title = _sb('comments_title');
	
	if ( !session_id() ) {
		session_start();
	}

	// ---------------
	// POST PROCESSING
	// ---------------

	// Verify information being passed
	global $ok, $error_message;
	$ok = true;
	$error_message = '';

	if ($ok) {
		// Check if comments are disabled. If so, this is probably a spam bot.
		if ($blog_config->getTag('BLOG_ENABLE_COMMENTS') == 0) {
			$commentsEnabled = false;
			$ok = false;
			$error_message = _sb('error_comments_disabled');
		} else {
			$commentsEnabled = true;
		}
	}
	
	if ($ok) {
		// Verify required fields exist (except comment_remember which is a check box...)
		$requiredFields = Array( 'y', 'm', 'entry', 'comment_capcha', 'comment_name', 'comment_email', 'comment_url', 'comment_text');
		$missingFields = Array();
		foreach ( $requiredFields as $fieldName ) {
			if ( !array_key_exists( $fieldName, $_POST ) ) {
				array_push( $missingFields, $fieldName );
			}
		}
		
		if ( count($missingFields) == 0 ) {
			$allFieldsExist = true;
		} else {
			$allFieldsExist = false;
			$ok = false;
			$error_message = _sb('error_fields_missing') . implode ( ', ', $missingFields );
		}
	}
	
	if ($ok) {
		// Check IP against Blacklist
		$oBlacklist = new CBlacklist;
		$oBlacklist->load( CONFIG_DIR.'blacklist.txt' );
		if ( $oBlacklist->isBanned( getIP() ) == true ) {
			$notBlackListed = false;
			$ok = false;
			$error_message = _('blacklisted');
		} else {
			$notBlackListed = true;
		}
	}
	
	if ($ok) {
		// Validate year, month and entry data format.
		if ( strpos( $_POST[ 'y' ], array( '/', '.', '\\', '%', '#', ';' ) ) === false && strlen( $_POST[ 'y' ] ) == 2 &&
			strpos( $_POST[ 'm' ], array( '/', '.', '\\', '%', '#', ';' ) ) === false && strlen( $_POST[ 'm' ] ) == 2 &&
			strpos( $_POST[ 'entry' ], array( '/', '.', '\\', '%', '#', ';' ) ) === false && strlen( $_POST[ 'entry' ] ) == 18 ) {
			// Dis-allow dots, and slashes to make sure the user is not able to
			// back-up a directory. Make sure the string lengths are correct.
			
			$postDataValid = true;
		} else {
			$postDataValid = false;
			$ok = false;
			$error_message = _('error_bad_data');
		}
	}
	
	if ($ok) {
		// Capcha Missing / Spambot Check
		if ( $logged_in == false && $_SESSION[ 'capcha_' . $_POST['entry' ] ] == '' ) {
			// Capcha did not exist in session, so comment poster did not come from the
			// comments page, where this should have been created. User is probably a spam robot.
			// Fix submitted by Jan Normann Nielsen via Sourceforge 2006-08-11
			$notSpamBot = false;
			$ok = false;
			$error_message = _sb('error_spambot');
		} else {
			$notSpamBot = true;
		}
	}
	
	if ($ok) {
		// Capcha Check
		if ( $_POST[ 'comment_capcha' ] == $_SESSION[ 'capcha_' . $_POST[ 'entry' ] ] || $logged_in ) {
			$capchaCorrect = true;
		} else {
			$capchaCorrect = false;
			$ok = false;
			$error_message = _sb('error_capcha');
		}
	}
	
	if ($ok) {
		// Banned Word Check (New 0.4.9)
		$oBannedWords = new CBannedWords;
		$oBannedWords->load( CONFIG_DIR.'bannedwordlist.txt' );
		if ( $oBannedWords->ContainsBannedWord( $_POST[ 'comment_name' ], $_POST[ 'comment_email' ], $_POST[ 'comment_url' ], $_POST[ 'comment_text' ] ) ) {
			$noBannedWords = false;
			$ok = false;
			$error_message = _sb('bannedword');
		} else {
			$noBannedWords = true;
		}
	}
	
	if ($ok) {
		// Entry Exists Check
		if ( entry_exists ( $_POST[ 'y' ], $_POST[ 'm' ], $_POST[ 'entry' ] ) ) {
			$entryExists = true;
		} else {
			$entryExists = false;
			$ok = false;
			$error_message = _sb('error_entry_missing');
		}
	}
	
	if ($ok) {
		// Check for empty name or comment text.
		if ( strlen( $_POST[ 'comment_name' ] ) == 0 || strlen( $_POST[ 'comment_text' ] ) == 0 ) {
			$notEmpty = false;
			$ok = false;
			$error_message = _sb('error_empty_text');
		} else {
			$notEmpty = true;
		}
	}
	
	if ($ok) {
		// Check if comment are already expired.
		$entry = $_POST[ 'entry' ];
		$year = substr($entry, 5, 2);
		$month = substr($entry, 7, 2);
		$day = substr($entry, 9, 2);
		if ( are_comments_expired( $month, $dday, $year ) ) {
			$notExpired = false;
			$ok = false;
			$error_message = _sb('expired_comment1') . $blog_config->getTag('BLOG_COMMENT_DAYS_EXPIRY') . _sb('expired_comment2');
		} else {
			$notExpired = true;
		}
		
	}
	
	if ($ok) {
		// Comment Moderation
		if ( $logged_in == false && $blog_config->getTag('BLOG_ENABLE_COMMENTS') ) {
			// Hold comment (don't show to regular users)
			$moderationFlag = 'H';
		} else {
			// Immediate release,
			$moderationFlag = '';
		}
		
		$comment_name = sb_stripslashes( $_POST[ 'comment_name' ] );
		$comment_email = sb_stripslashes( $_POST[ 'comment_email' ] );
		$comment_url = sb_stripslashes( $_POST[ 'comment_url' ] );
		$comment_text = sb_stripslashes( $_POST[ 'comment_text' ] );
		
		$result = write_comment( $_POST[ 'y' ], $_POST[ 'm' ], $_POST[ 'entry' ],
			$comment_name,
			$comment_email,
			$comment_url,
			$comment_text,
			getIP(),
			$moderationFlag,
			time() );
		
		// Save remember me cookie
		if ( $_POST[ 'comment_remember' ] != 0 ) {
			setcookie( 'comment_name', $comment_name, time()+60*60*24*30);
			setcookie( 'comment_email', $comment_email, time()+60*60*24*30);
			setcookie( 'comment_url', $comment_url, time()+60*60*24*30);
		}
		
		// Delete entry capcha session
		@session_unregister( 'capcha_' . $_GET[ 'entry' ] );
		
		if ( $result === true ) {
			// comment was successfully written!
		} else {
			// problem writing file or couldn't create folder.
			$ok = false;
			$error_message = _sb('comments_error_add') . $result;
		}
	}
	
	/*
	if ( $ok == true ) {
		$relative_url = 'comments.php?y='.$_POST[ 'y' ].'&m='.$_POST[ 'm' ].'&entry='.$_POST[ 'entry' ];
		redirect_to_url( $relative_url );
	}
	*/
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $logged_in, $ok, $error_message;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('comments_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( $ok == true ) {
			echo( _sb('success_add') . '<p />' );
			
			if ( $blog_config->getTag('BLOG_COMMENTS_MODERATION') ) {
				if ( $logged_in == false ) {
					echo(_sb('user_notice_mod') . '<p />');
				}
			}
			
			$relative_url = 'comments.php?y='.$_POST[ 'y' ].'&m='.$_POST[ 'm' ].'&entry='.$_POST[ 'entry' ];
			echo( '<a href="' . $relative_url . '">' . _sb('return_to_comments') . '</a><p />' );
		} else {
			echo( $error_message . '<p />' );
		}
		echo( '<a href="index.php">' . _sb('home') . '</a>' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
