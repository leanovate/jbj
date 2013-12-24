<?php
	// -------------------------------
	// Simple PHP Blog Add Blocks File
	// -------------------------------
	//
	// Name: Modern Theme v2
	// Author: Alexander Palmo
	// Version: 0.4.5b
	// Revised by Ridgarou

	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('add_block_title');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	if ( isset( $_POST[ 'block_name' ] ) ) {
		if ( !isset($_POST[ 'block_id' ] ) ) {
			$_POST[ 'block_id' ] = '';
		}
		$ok = write_block( sb_stripslashes( $_POST[ 'block_name' ] ), sb_stripslashes( $_POST[ 'block_content' ] ), $_POST[ 'block_id' ] );
	}
	
	if ( isset( $_GET[ 'action' ] ) ) {
		$action = sb_stripslashes( $_GET[ 'action' ] );
		if ( $action === 'edit' ) {
			$block_id = sb_stripslashes( $_GET[ 'block_id' ] );
			// nothing
		} elseif ($action === 'enable') {
		} elseif ($action === 'disable') {
		} else {
			$ok = modify_block( $action, sb_stripslashes( $_GET[ 'block_id' ] ) );
		}
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $block_id, $block_name, $block_content, $action, $theme_vars;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('add_block_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		
		// Create array.
		$str = NULL;
		//if ( $result ) {	
	
			$block_content = '';
			$block_name = '';
			if ($action != "edit") {
				$block_id = NULL;
			}
			
			$array = get_blocks();
			for ( $i = 0; $i < count( $array ); $i+=2 ) {
				// Create HTML
				$hasoptions = FALSE;
				
				// 1 - Name of Block
				$str	.= ( 1 + ($i/2) ) . ' - ' . $array[$i];
				if ($array[$i+1] == 'plugin' AND class_exists($array[$i])) {
					$plugin = new $array[$i];
					$hasoptions = $plugin->getOptions();
					$str	.= ' <a href="plugins.php">(Plugin)</a> ';
				}
				$str	.= '<br />';
				
				//	up | down | edit | delete
				$str	.= '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
				if ( $i > 1 ) {
					$str	.= '<a href="add_block.php?action=up&block_id='.$i.'">' . _sb('up') . '</a> | ';
				} else {
					$str	.= _sb('up') . ' | ';
				}
				if ( $i < ( count( $array ) - 2 ) ) {
					$str	.= '<a href="add_block.php?action=down&block_id='.$i.'">' . _sb('down') . '</a>';
				} else {
					$str	.= _sb('down');
				}
				if ($array[$i+1] != 'plugin') {
					$str	.= ' | <a href="add_block.php?action=edit&block_id='.$i.'">' . _sb('edit') . '</a> | ';
					$str	.= '<a href="add_block.php?action=delete&block_id='.$i.'">' . _sb('delete') . '</a> ';
				} elseif ($hasoptions) {
					$str	.= ' | <a href="plugins.php?options=' . $array[$i] . '">options</a>';
				}
				$str	.= '<br /><br />';
				if ( $action === "edit" && $i == $block_id ) {
					$block_name = $array[$i];
					$block_content = $array[$i+1];
				} 
			}
			
			
			if ($action === "edit") {
				echo _sb('add_block_instructions_edit') . '<p />';
			} else {
				echo _sb('add_block_instructions_modify') . '<p />';
			}
		
		echo( $str );
		
		echo( '<hr />' );

                // PREVIEW

                $editor = sb_editor('static');
                echo( $editor['preview'] );

	?>
	
		<form action='add_block.php' method="post" name="editor" id="editor">
		
			<label for="blog_subject"><?php echo (_sb('block_name') ); ?></label><br />
			<input type="text" name="block_name" autocomplete="OFF" value="<?php echo $block_name; ?>" size="40"><br /><br />
			
			<?php sb_editor_controls('text'); ?>
			
			<label for="text"><?php echo( _sb('block_content') ); ?></label><br />
			<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="text" name="block_content" rows="20" cols="50" autocomplete="OFF"><?php echo $block_content; ?></textarea><br /><br />
			
			<?php if( isset( $block_id ) ) { ?>
			<input type="hidden" name="block_id" value="<?php echo $block_id; ?>" />
			<?php } ?>
			<input type="submit" name="submit" value="&nbsp;<?php if ( isset ( $block_id ) && $action === 'edit' ) { echo _sb('add_block_submit_btn_edit'); } else { echo _sb('add_block_submit_btn_add'); } ?>&nbsp;" onclick="this.form.action='add_block.php';" />
		</form>
		
		<?php
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
