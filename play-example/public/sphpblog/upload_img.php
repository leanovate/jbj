<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('upload_img_title');
	require_once('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('upload_img_title');
			
		// PAGE CONTENT BEGIN
		ob_start(); ?>

		<?php
		$formstate = $_REQUEST['formstate'];
		if (!ini_get('file_uploads')) {
			echo( _sb('upload_img_error') . 'php.ini file_uploads = Off.<p />' );
		}
		elseif($formstate !="showuploaders"){
	echo('<form action="upload_img.php" method="get">'. _sb('howmany') .' (Total Max: ' . intval(return_bytes(ini_get('post_max_size')))/(1024*1024) . ' MB) <select id="howmany" name="howmany">');
		for($i=1;$i<=intval(ini_get('max_file_uploads'));$i++){
			echo('<option value="'.$i.'">'.$i.'</option>');
		}
echo <<<EOF
	</select>
	<input name="formstate" type="hidden" value="showuploaders" />
	<input type="submit" value="next &raquo;" />
		</form>
EOF;
		} else {
			$howmany = $_REQUEST['howmany'];
			$formstate = "notshowuploaders";
			
			echo( _sb('upload_img_instructions') );
			echo(' (Total Max: ' . intval(return_bytes(ini_get('post_max_size')))/(1024*1024) . ' MB) <p />');
		
			echo('<form enctype="multipart/form-data" action="upload_img_cgi.php" method="POST">');
			echo( _sb('select_file') );
			echo(' (Max ' . intval(return_bytes(ini_get('upload_max_filesize')))/(1024*1024) . ' MB each)<br /><br />');
			for($i=1;$i<=$howmany;$i++){
				echo("<input name=\"userfile[]\" type=\"file\"><br />");			
			}
			echo("<input name=\"howmany\" type=\"hidden\" value=\"$howmany\" />");
			echo("<input type=\"submit\" value=\"". _sb('upload_btn') ."\">"); 
			echo("</form>");
		}		
		?>
		<?php
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
