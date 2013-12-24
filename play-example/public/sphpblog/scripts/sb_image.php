<?php 

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com
	
	// ----------------------
	// Image Upload Functions
	// ----------------------
	
	
	function image_list () 
		{
		// Get a list of images in the image folder. Return HTML.
		//
		if (!file_exists(IMAGES_DIR)) 
			{
			$oldumask = umask(0);
			$ok = mkdir(IMAGES_DIR, BLOG_MASK );
			umask($oldumask);
			}
		
		// Changed this to only display Image files. This function
		// is used in comment.php if the blog owner has img tag
		// enabled for comments. (09/08/05 - alex)
		$dir = BASEURL . "images/";
		$contents = sb_folder_listing( IMAGES_DIR, array( '.jpg', '.jpeg', '.gif', '.png' ) );
		// $contents = sb_folder_listing( $dir, array() );
		
		$str = NULL;
		if ($contents)
			{
			for ( $i = 0; $i < count( $contents ); $i++ )
				{
				if ($contents[$i] != '.htaccess' && $contents[$i] != 'Thumbs.db')
					{
					$str  .= '<img src="thumb.php?file=' . $contents[$i] . '"><br />';
					$str  .= '<a href="'.$dir.$contents[$i].'" target=_blank>'.$contents[$i].'</a><br /><br />
'; 
					}
				}
			}
		return ( $str );
		}
?>
