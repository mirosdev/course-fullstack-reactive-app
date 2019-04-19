import { Injectable } from '@angular/core';
import {PostModel} from './post.model';
import {Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Router} from '@angular/router';
import {PostsPaginatedModel} from './posts-paginated.model';

const GET_ALL_POSTS_PAGINATED = '/post/all/';
const ADD_NEW_POST = '/post/save';
const DELETE_POST = '/post/delete/';
const GET_SINGLE_POST = '/post/single/';

@Injectable({
  providedIn: 'root'
})
export class PostsService {

  private posts: PostModel[] = [];
  private postsUpdated = new Subject<PostModel[]>();
  private totalPosts = new Subject<number>();

  constructor(private httpClient: HttpClient,
              private router: Router) { }

  getPosts(pageIndex: number, pageSize: number) {
    this.httpClient.get<PostsPaginatedModel>(environment.apiUrl + GET_ALL_POSTS_PAGINATED + pageIndex + '/' + pageSize)
      .subscribe(
        postData => {
          this.posts = postData.posts;
          this.postsUpdated.next(postData.posts);
          this.totalPosts.next(postData.totalItemsCount);
        });
  }

  getPost(postId: string) {
    return this.httpClient.get<PostModel>(environment.apiUrl + GET_SINGLE_POST + postId);
  }

  getPostUpdateListener() {
    return this.postsUpdated.asObservable();
  }

  getTotalPostsListener() {
    return this.totalPosts.asObservable();
  }

  addPost(titleInput: string, contentInput: string, image: File) {
    const postData = new FormData();
    postData.append('title', titleInput);
    postData.append('content', contentInput);
    postData.append('image', image, image.name);
    this.httpClient.post<PostModel>(environment.apiUrl + ADD_NEW_POST, postData)
      .subscribe(
        response => {
          if (!this.posts) {
            this.posts = [];
          }
          this.posts.push(response);
          this.postsUpdated.next([...this.posts]);
          this.router.navigate(['/']);
        });
  }

  updatePost(post: PostModel, image: File | string) {
    let postData: PostModel | FormData;
    if (typeof(image) === 'object') {
      postData = new FormData();
      postData.append('id', post.id);
      postData.append('title', post.title);
      postData.append('content', post.content);
      postData.append('username', post.username);
      postData.append('image', image, image.name);
    } else {
      postData = {
        id: post.id,
        content: post.content,
        title: post.title,
        username: post.username,
        imagePath: image
      };
    }
    this.httpClient.post<PostModel>(environment.apiUrl + ADD_NEW_POST, postData)
      .subscribe(
        response => {
          post.imagePath = response.imagePath;
          const updatedPosts = [...this.posts];
          const oldPostIndex = updatedPosts.findIndex(p => p.id === post.id);
          updatedPosts[oldPostIndex] = post;
          this.posts = updatedPosts;
          this.postsUpdated.next([...this.posts]);
          this.router.navigate(['/']);
        }
      );
  }

  deletePost(postId: string, username: string) {
    this.httpClient.delete<boolean>( environment.apiUrl + DELETE_POST + postId + '/' + username)
      .subscribe(
        next => {
          if (next === true) {
            this.posts = this.posts.filter(post => post.id !== postId);
            this.postsUpdated.next([...this.posts]);
          }
        });
  }
}
