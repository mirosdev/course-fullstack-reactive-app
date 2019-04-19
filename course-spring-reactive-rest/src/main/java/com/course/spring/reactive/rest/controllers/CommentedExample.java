//@RestController
//public class ResourceREST {
//
//    @RequestMapping(value = "/resource/user", method = RequestMethod.GET)
//    @PreAuthorize("hasRole('USER')")
//    public Mono<ResponseEntity<?>> user() {
//        return Mono.just(ResponseEntity.ok(new Message("Content for user")));
//    }
//
//    @RequestMapping(value = "/resource/admin", method = RequestMethod.GET)
//    @PreAuthorize("hasRole('ADMIN')")
//    public Mono<ResponseEntity<?>> admin() {
//        return Mono.just(ResponseEntity.ok(new Message("Content for admin")));
//    }
//
//    @RequestMapping(value = "/resource/user-or-admin", method = RequestMethod.GET)
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//    public Mono<ResponseEntity<?>> userOrAdmin() {
//        return Mono.just(ResponseEntity.ok(new Message("Content for user or admin")));
//    }
//}
