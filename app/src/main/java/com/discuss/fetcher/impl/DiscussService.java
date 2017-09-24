package com.discuss.fetcher.impl;


import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.Response;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Url;

import rx.Observable;

/**
 * @author Deepak Thakur
 *
 */
interface DiscussService {

    @GET
    Observable<Response<List<Question>>> getQuestions(@Url final String pathParams);

    @GET
    Observable<Response<List<Comment>>> getCommentsForQuestion(@Url final String pathParams);

    @GET
    Observable<Response<List<Question>>> getBookMarkedQuestions(@Url final String pathParams);

    @GET
    Observable<Response<List<Question>>> getLikedQuestions(@Url final String pathParams);

    @GET
    Observable<Response<List<Comment>>> getUserAddedComments(@Url final String pathParams);

    @GET
    Observable<Response<Question>> getQuestion(@Url final String pathParams);

    @GET
    public boolean likeQuestion(@Url final String pathParams); /* @todo Make it PUT  */

    @GET
    public boolean likeComment(@Url final String pathParams);  /* @todo Make it PUT  */

    @GET
    public boolean bookmarkQuestion(@Url final String pathParams);  /* @todo Make it PUT  */

}