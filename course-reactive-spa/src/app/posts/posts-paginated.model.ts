import {PostModel} from './post.model';

export interface PostsPaginatedModel {
  totalItemsCount: number;
  posts: PostModel[];
}
