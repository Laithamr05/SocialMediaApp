// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

public class PostCreator { // factory class for creating posts

	public Post createPost(String postID, UserManager creator, String content, String creationDate,
			CircularDoublyLinkedList<UserManager> sharedWith, boolean shareWithAllFriends) { // method to create a new post
		Post newPost = new Post(postID, creator, content, creationDate, sharedWith); // create post with provided data

		if (shareWithAllFriends) { // check if post should be shared with all friends
			newPost.shareWithAllFriends(); // share with all friends of creator
		}

		return newPost; // return the created post
	}

}
