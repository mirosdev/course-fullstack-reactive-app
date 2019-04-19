import {Component, OnDestroy, OnInit} from '@angular/core';
import {PostModel} from '../post.model';
import {PostsService} from '../posts.service';
import {Subscription} from 'rxjs';
import {PageEvent} from '@angular/material';
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit, OnDestroy {

  posts: PostModel[] = [];
  private postsSub: Subscription;
  private postsCountSub: Subscription;
  isLoading = false;
  private authStatusSub: Subscription;
  userIsAuthenticated = false;
  username: string;

  totalPosts = 10;
  pageIndex = 0;
  postsPerPage = 5;
  pageSizeOptions = [1, 2, 5, 10];

  constructor(private postsService: PostsService,
              private authService: AuthService) { }

  ngOnInit() {
    this.isLoading = true;
    this.postsService.getPosts(this.pageIndex, this.postsPerPage);
    this.username = this.authService.getUsername();
    this.postsSub = this.postsService.getPostUpdateListener()
      .subscribe(
        (posts: PostModel[]) => {
          this.isLoading = false;
          this.posts = posts;
        });

    this.postsCountSub = this.postsService.getTotalPostsListener()
      .subscribe(
        itemsCount => {
          this.totalPosts = itemsCount;
        });

    this.userIsAuthenticated = this.authService.getIsAuth();

    this.authStatusSub = this.authService.getAuthStatusListener()
      .subscribe(
        isAuthenticated => {
          this.userIsAuthenticated = isAuthenticated;
          this.username = this.authService.getUsername();
        });
  }

  onChangedPage(pageData: PageEvent) {
    this.isLoading = true;
    this.pageIndex = pageData.pageIndex;
    this.postsPerPage = pageData.pageSize;
    this.postsService.getPosts(this.pageIndex, this.postsPerPage);
  }

  onDelete(postId: string, username: string) {
    this.totalPosts = this.totalPosts - 1;
    this.postsService.deletePost(postId, username);
  }

  ngOnDestroy(): void {
    this.postsSub.unsubscribe();
    this.postsCountSub.unsubscribe();
    this.authStatusSub.unsubscribe();
  }

}
