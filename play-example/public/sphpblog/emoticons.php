<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('emoticons_title');
	require('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $user_colors;
		
		// -------------------------------
		// Emoticon code by NoWhereMan and Hydra
		// -------------------------------
		//
		// Authors:
		// NoWhereMan - http://www.nowhereland.it.pn/
		// Hydra - http://samyweb.altervista.org/
		//
		// Additional Thanks to:
		// Drudo - http://drudo.altervista.org/
		// SPBItalia Forum - http://spbitalia.altervista.org/

		function upload_emoticons() {
			// Emoticon upload form results
			$path = IMAGES_DIR.'emoticons';
			$uploaddir = $path;
			
			$ok = false;
			if ( $_FILES[ 'user_emot' ][ 'error' ] == 0 ) {
				if (!file_exists($uploaddir)) {
					$oldumask = umask(0);
					@mkdir($uploaddir, BLOG_MASK );
					@umask($oldumask);
				}
				
				$uploaddir .= '/';
				$uploadfile = $uploaddir . preg_replace("/ /","_",$_FILES[ 'user_emot' ][ 'name' ]);
		
				if ( @is_uploaded_file($_FILES['user_emot']['tmp_name'] ) ) {
			                // Allowed files
			                $upload_valid_extentions = array( "jpg", "gif", "png" );
			                $extension = strtolower(substr(strrchr($_FILES['user_emot']['tmp_name'], "."), 1));
			                if (!in_array($extension, $upload_valid_extentions)) {
                        			$ok = -1;
					} else {
						if ( @move_uploaded_file($_FILES[ 'user_emot' ][ 'tmp_name' ], $uploadfile ) ) {
							chmod( $uploadfile, BLOG_MASK );
							$ok = true;
						}
					}
				}
			}
			
			return $ok;
		}

		function emoticons_admin_display() {
			global $theme_vars;
			// Emoticon table
			$emo = emoticons_load();
			
			
			$str_out = '<form enctype="multipart/form-data" name="emoticons" method="post" action="emoticons.php">';
			$str_out .= "\n\t<table width=\"". $theme_vars[ 'max_image_width' ] . "\">\n";
			
			for ( $i = 0; $i < count ($emo); $i++) {
				
				$tags=emoticons_check_tags($emo[$i]);
				
				$str_out .=	 "\t\t<tr>\n";
				$str_out .=	 "\t\t\t<td><input type=\"checkbox\" name=\"emo$i\" ";
										if ($tags) { $str_out .=	'checked="checked" '; }
															$str_out .=	 "/></td>\n";
				$str_out .=	 "\t\t\t<td><img src=\"" . $emo[$i] . "\" alt=\"emo$i\" />";
						$str_out .=	 "<input type=\"hidden\" name=\"emo_name$i\" value=\"" . encode_input_value($emo[$i]) . '" />';
				$str_out .=	 "</td>\n";
				$str_out .=	 "\t\t\t<td>$emo[$i]</td>\n";
				$str_out .=	 "\t\t\t<td><input type=\"text\" name=\"tag$i\" value=\"" . encode_input_value($tags) . "\" autocomplete=\"OFF\" /></td>\n";
				$str_out .=	 "\t\t</tr>\n";
			
			}
		
			$str_out .=	 '<input type="hidden" name="emo_count" value="' . count($emo) . '" />';
			$str_out .=	 "\t</table>\n\t";
			
			$str_out .=	 '<hr />';
			$str_out .=	 "\t<input type=\"submit\" value=\"". _sb('save_button')."\" />\n";
			$str_out .=	 "</form>\n";
			
			return $str_out;
		}
		
		function emoticons_getform() {
			// Emoticon preferences form results
		
			$form_arr = Array();
			for ( $i = 0; $i < $_POST['emo_count']; $i++) {
				if ($_POST['emo' . $i] == 'on') {
					if ($_POST['tag' . $i] !== '') {
						$temp_arr = Array();
						$temp_arr['PATH'] = ($_POST['emo_name' . $i]);
						$temp_arr['TAGS'] = ($_POST['tag' . $i]);
						array_push( $form_arr, $temp_arr );
					}
				}
			}
			
			// echo('<pre>');
			// print_r($form_arr);
			// echo('</pre>');
			
			return $form_arr;
		}
	
		function emoticons_save($form_arr) {
			// Convert array format to a string.
			
			$str = '';
			$emote_arr = Array();
			for ( $i = 0; $i < count($form_arr); $i++) {
				$temp_arr = Array();
				$tags = $form_arr[$i]['TAGS'];
				$tags = str_replace( '|', '&#124;', $tags );
				$tags = str_replace( '=', '&#61;', $tags );
				
				$temp_arr['PATH'] = $form_arr[$i]['PATH'];
				$temp_arr['TAGS'] = sb_stripslashes($tags);
				
				array_push( $emote_arr, implode_with_keys( $temp_arr, '=' ) );
			}
			$str = implode( '|', $emote_arr );
			
			return sb_write_file( CONFIG_DIR.'emoticons.txt', $str );
		}
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('emoticons_title');
		
		// PAGE CONTENT BEGIN
		ob_start(); ?>
		<?php echo( _sb('emoticons_instructions') ); ?><p />

		<?php if (ini_get('upload_files')) { ?>		
		<hr />		
		<!-- Upload New Emoticon Form -->
		<form enctype="multipart/form-data" name="emoticons_up" method="post" action="emoticons.php">
			<?php echo( _sb('upload_instructions') ); ?> <input name="user_emot" type="file" /><input type="submit" value="Upload" />
		</form>
		<!-- Upload New Emoticon Form -->
		
		<?php 
		}

		if ($_FILES["user_emot"]) {
			// User is trying to upload a graphic.
			$ok = upload_emoticons();
			switch( $ok ) {
				case true:
					echo( _sb("upload_success") );
					break;
				case false:
					echo( _sb("upload_error") );
					break;
				case -1:
					echo( _sb("upload_invalid") );
					break;
			}
		}
		
		echo '<hr />';
		
		if (!$_FILES["user_emot"] && $_POST && !$_POST["user_emot"]) {
			// User is updating emoticon preferences.
			$form_arr = emoticons_getform();
			if ( emoticons_save($form_arr) ) {
				echo( _sb("save_success") );
			} else {
				echo( _sb("save_error") );
			}
		}
		
		echo( emoticons_admin_display() );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
