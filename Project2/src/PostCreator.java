public class PostCreator {

	public class PostManager {

		public Post createPost(String postID, UserManager creator, String content, String creationDate,
				CircularDoublyLinkedList<UserManager> sharedWith, boolean shareWithAllFriends) {
			Post newPost = new Post(postID, creator, content, creationDate, sharedWith);

			if (shareWithAllFriends) {
				newPost.shareWithAllFriends();
			}

			else {
				if (sharedWith.size() == 0) {
					System.out.println("No users to share with.");
				}
			}

			return newPost;
		}
	}

}
