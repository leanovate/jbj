<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in, $theme_vars;
	$logged_in = logged_in( true, true );

	// Extra Javascript
	ob_start();
?>
<script type="text/javascript">
	// <!--
	
	// Update Text Fields
	function set_hex ( hex_val ) {
		var str;
		for (i=0; i<document.forms[ 'colors' ][ 'area' ].length; i++) {
			if (document.forms[ 'colors' ][ 'area' ][i].checked == true) {
				str = document.forms[ 'colors' ][ 'area' ][i].value;
			}
		}
		document.forms[ 'colors' ][str].value = hex_val;
		changeColor(str, "#"+hex_val);
	}
	
	// Change Swatch Color
	function changeColor(area, whichColor) {
		document.getElementById(area+'_swatch').style.backgroundColor = whichColor;
	}
	
	function load_preset() {
		// alert( 'hello' );
		str = document.forms[ 'colors' ][ 'presets' ].value;
		if ( str != '--' ) {
			arr = str.split('|');
			for (i=4; i<arr.length; i=i+2) {
				id = arr[i];
				hex = arr[i+1];
				document.forms[ 'colors' ][id].value = hex;
				document.getElementById(id+'_swatch').style.backgroundColor = "#"+hex;
			}
			document.getElementById("scheme_name").value = arr[0];
			document.getElementById("scheme_file").value = arr[1];
		}
	}
	
	// -->
</script>
<?php
	$head .= ob_get_clean();
	
	// Page Title
	$page_title = _sb('colors_title');
	
	// ---------------
	// POST PROCESSING
	// ---------------
if (isset($_POST['submit']) OR isset($_POST['save_btn'])) {
        $color_def = theme_default_colors();
        $post_array = array();
        array_push( $post_array, 'name' );
        if ( array_key_exists( 'save_btn', $_POST ) == true && $_POST[ 'scheme_name' ] != '' && $_POST[ 'scheme_file' ] != '' ) {
                $str = str_replace( '|', ':', sb_stripslashes( $_POST[ 'scheme_name' ] ) );
                array_push( $post_array, $str );
        } else {
                array_push( $post_array, 'custom' );
        }

        for ( $i = 0; $i < count( $color_def ); $i++ ) {
                $id = $color_def[$i][ 'id' ];
                $color = sb_stripslashes( $_POST[ $id ] );
                array_push( $post_array, $id );
                array_push( $post_array, $color );
        }

        // Check if we should save color scheme, or just update colors on web site.
        if ( array_key_exists( 'save_btn', $_POST ) == true && $_POST[ 'scheme_name' ] != '' && $_POST[ 'scheme_file' ] != '' ) {
                $filename = sb_stripslashes( $_POST[ 'scheme_file' ] );
                $filename = preg_replace( '/(\s|\\\|\/|%|#)/', '_', $filename ); // Replace whitespaces [\n\r\f\t ], slashes, % and # with _
                $ok = write_colors( $post_array, $filename );
        } else {
                $ok = write_colors( $post_array, NULL );
        }
}	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $user_colors, $theme_vars, $blog_theme, $ok;		
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('colors_title');
	
		// PAGE CONTENT BEGIN
		ob_start();
		if (isset($ok)) { 
			if ( $ok === false ) {
        	                echo( _sb('colors_error') . $ok . '<p />' );
	                } else {
                       		echo _sb('colors_success') . '<p />';
                	}
		}
		echo( _sb('colors_instructions') ); ?><p />		
		
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" 
			codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" 
			width="390" height="390" id="color_picker" align="">
			<param name=movie value="flash/color_picker.swf"> 
			<param name=quality value=high> 
			<param name=bgcolor value=#E6E6E6> 
			<embed src="flash/color_picker.swf" quality=high bgcolor=#E6E6E6 
				width="390" height="390" name="color_picker" align="" 
				type="application/x-shockwave-flash" 
				pluginspage="http://www.macromedia.com/go/getflashplayer">
			</embed>
		</object><p />
		
		<form method="post" name="colors" id="colors" action="">
			<?php
				global $user_colors, $theme_vars;
				$color_def = theme_default_colors();
				
				$left_count = floor( count( $color_def ) / 2 );
				
				echo ('<table border="0" width="' . $theme_vars[ 'max_image_width' ] . '" cellspacing="0" cellpadding="0">' . "\n");
				// Left Column
				echo ('<tr align="left" valign="top">' . "\n");
				echo ('<td width="50%">' . "\n");
				for ( $i = 0; $i < $left_count; $i++ ) {
					$id = $color_def[$i][ 'id' ];
					$color = $user_colors[$id];
					$string = $color_def[$i][ 'string' ];
					$str = '';
					if ( $i == 0 ) {
						$str	.= '<input type="radio" name="area" value="' . $id . '" checked> ';
					} else {
						$str	.= '<input type="radio" name="area" value="' . $id . '"> ';
					}
					$str	.= '<span class="swatch" style="background: #' . $color . ';" id="' . $id . '_swatch">&nbsp;&nbsp;&nbsp;&nbsp;</span> ';
					$str	.= '<input type="text" name="' . $id . '" value="' . $color . '" size="7" maxlength="6"> ' . $string . '<br />' . "\n";
					echo ( $str );
				}
				echo ('</td>' . "\n");
				// Right Column
				echo ('<td width="50%">' . "\n");
				for ( $i = $left_count; $i < count( $color_def ); $i++ ) {
					$id = $color_def[$i][ 'id' ];
					$color = $user_colors[$id];
					$string = $color_def[$i][ 'string' ];
					$str = '';
					if ( $i == 0 ) {
						$str	.= '<input type="radio" name="area" value="' . $id . '" checked> ';
					} else {
						$str	.= '<input type="radio" name="area" value="' . $id . '"> ';
					}
					$str	.= '<span class="swatch" style="background: #' . $color . ';" id="' . $id . '_swatch">&nbsp;&nbsp;&nbsp;&nbsp;</span> ';
					$str	.= '<input type="text" name="' . $id . '" value="' . $color . '" size="7" maxlength="6"> ' . $string . '<br />' . "\n";
					echo ( $str );
				}
				echo ('</td>' . "\n");
				echo ('</tr>' . "\n");
				
				echo ('</table><p />' . "\n");
			
				echo ('<input type="submit" name="submit" value="' . _sb('submit_btn') . '"/>' );
				echo( '<hr />' . "\n" );
				
				// Preset Color Dropdown
				echo ('<label for="presets">' . _sb('color_preset') . '</label><br />' . "\n");
				echo ('<select name="presets" id="presets" onChange="load_preset();">' . "\n");
				echo( '<option label="--" value="--">--</option>' . "\n");
				
				global $blog_theme;
				
				// Default Theme Colors
				$dir = 'themes/' . $blog_theme . '/colors/';
				$color_files = sb_folder_listing( $dir, array( '.txt' ) );
				
				for ( $i = 0; $i < count( $color_files ); $i++ ) {
					$result = sb_read_file( $dir . $color_files[ $i ] );
					if ( $result ) {
						$saved_colors = explode( '|', $result );
						
						if ( count( $saved_colors ) >= 2 ) {
							$preset_name = $saved_colors[1];
							$preset_file = substr( $color_files[ $i ], 0, -4);
							
							$str = '<option label="' . $preset_name . '" value="' . $preset_name . '|' . $preset_file . '|' . $result . '"';
							$str	.= '>' . $preset_name . '</option>' . "\n";
							
							echo( $str );
						}
					}
				}
				
				// Saved User Colors
				$dir = CONFIG_DIR.'schemes/';
				$color_files = sb_folder_listing( $dir, array( '.txt' ) );
				if ( count( $color_files ) > 0 ) {
					echo( '<option label="--" value="--">--</option>' . "\n");
					for ( $i = 0; $i < count( $color_files ); $i++ ) {
						$result = sb_read_file( $dir . $color_files[ $i ] );
						if ( $result ) {
							$saved_colors = explode( '|', $result );
							
							if ( count( $saved_colors ) >= 2 ) {
								$preset_name = $saved_colors[1];
								$preset_file = substr( $color_files[ $i ], 0, -4);
								
								$str = '<option label="' . $preset_name . '" value="' . $preset_name . '|' . $preset_file . '|' . $result . '"';
								$str	.= '>' . $preset_name . '</option>' . "\n";
								
								echo( $str );
							}
						}
					}
				}
				
				echo ('</select><br /><br />');				
			?>
			
			<label for="scheme_name"><?php echo( _sb('scheme_name') ); ?></label><br />
			<input type="text" name="scheme_name" id="scheme_name" autocomplete="OFF" value="" size="40" maxlength="32"><br /><br />
			
			<label for="scheme_file"><?php echo( _sb('scheme_file') ); ?></label><br />
			<input type="text" name="scheme_file" id="scheme_file" autocomplete="OFF" value="" size="40" maxlength="32"> <input type="submit" name="save_btn" value="<?php echo( _sb('save_btn') ); ?>" />
		
		</form>
		
		<?php
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		if ( $theme_vars[ 'options' ][ 'disallow_colors' ] == 1 ) { 
			$entry_array[ 'entry' ] = _sb('theme_doesnt_allow_colors');
		}
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
