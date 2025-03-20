package org.warm4ik.crud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.model.Writer;
import org.warm4ik.crud.repository.PostRepository;
import org.warm4ik.crud.service.PostService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private static Post testPost;

    @BeforeAll
    static void setUp() {
        testPost = Post.builder()
                .id(1)
                .content("Test")
                .created(new Date(1700000000000L))
                .updated(new Date(1700000000000L))
                .writer(new Writer())
                .labels(new ArrayList<>())
                .postStatus(Status.ACTIVE)
                .build();
    }

    @Test
    void getPostById() {
        when(postRepository.getById(eq(1))).thenReturn(testPost);
        Post post = postService.getPostById(1);

        assertNotNull(post);
        assertEquals(testPost.getContent(), post.getContent());
        assertEquals(testPost.getCreated(), post.getCreated());
        assertEquals(testPost.getWriter(), post.getWriter());
        assertEquals(testPost.getLabels(), post.getLabels());
        assertEquals(testPost.getPostStatus(), post.getPostStatus());

        verify(postRepository, times(1)).getById(eq(1));
    }

    @Test
    void getAllPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(testPost);

        when(postRepository.getAll()).thenReturn(posts);
        List<Post> result = postService.getAllPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(posts.get(0).getContent(), result.get(0).getContent());
        assertEquals(posts.get(0).getCreated(), result.get(0).getCreated());
        assertEquals(posts.get(0).getWriter(), result.get(0).getWriter());
        assertEquals(posts.get(0).getLabels(), result.get(0).getLabels());
        assertEquals(posts.get(0).getPostStatus(), result.get(0).getPostStatus());

        verify(postRepository, times(1)).getAll();
    }

    @Test
    void savePost() {
        when(postRepository.save(eq(testPost))).thenReturn(testPost);

        Post post = postService.savePost(testPost);

        assertNotNull(post);
        assertEquals(testPost.getContent(), post.getContent());
        assertEquals(testPost.getCreated(), post.getCreated());
        assertEquals(testPost.getWriter(), post.getWriter());
        assertEquals(testPost.getLabels(), post.getLabels());
        assertEquals(testPost.getPostStatus(), post.getPostStatus());

        verify(postRepository, times(1)).save(eq(testPost));
    }

    @Test
    void updatePost() {
        Post updatedPost = Post.builder()
                .id(1)
                .content("Test updated")
                .created(new Date(1800000000000L))
                .updated(new Date(1800000000000L))
                .writer(new Writer())
                .labels(new ArrayList<>())
                .postStatus(Status.ACTIVE)
                .build();

        when(postRepository.update(eq(updatedPost))).thenReturn(updatedPost);
        Post postResult = postService.updatePost(updatedPost);

        assertNotNull(postResult);
        assertEquals(updatedPost.getContent(), postResult.getContent());
        assertEquals(updatedPost.getCreated(), postResult.getCreated());
        assertEquals(updatedPost.getWriter(), postResult.getWriter());
        assertEquals(updatedPost.getLabels(), postResult.getLabels());
        assertEquals(updatedPost.getPostStatus(), postResult.getPostStatus());

        verify(postRepository, times(1)).update(eq(updatedPost));
    }

    @Test
    void deletePost() {
        doNothing().when(postRepository).deleteById(eq(1));
        postService.deletePost(1);

        verify(postRepository, times(1)).deleteById(eq(1));
    }
}