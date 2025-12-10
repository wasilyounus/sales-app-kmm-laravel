<?php

namespace Tests\Unit\Middleware;

use App\Http\Middleware\EnsureUserHasCompany;
use App\Models\Account;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Http\Request;
use Tests\TestCase;

class EnsureUserHasCompanyTest extends TestCase
{
    use RefreshDatabase;
    protected $middleware;
    protected $user;
    protected $account;

    protected function setUp(): void
    {
        parent::setUp();

        $this->middleware = new EnsureUserHasCompany();
        $this->user = User::factory()->create();
        $this->account = Account::factory()->create();

        $this->user->accounts()->attach($this->account->id, ['role' => 'admin']);
    }

    /** @test */
    public function allows_request_when_user_has_current_account_in_session()
    {
        $this->actingAs($this->user);
        session(['current_account_id' => $this->account->id]);

        $request = Request::create('/admin/dashboard', 'GET');
        $request->setUserResolver(fn() => $this->user);

        $response = $this->middleware->handle($request, function ($req) {
            return response('OK', 200);
        });

        $this->assertEquals(200, $response->getStatusCode());
    }

    /** @test */
    public function allows_request_when_user_has_current_account_in_database()
    {
        $this->actingAs($this->user);
        $this->user->update(['current_account_id' => $this->account->id]);

        $request = Request::create('/admin/dashboard', 'GET');
        $request->setUserResolver(fn() => $this->user);

        $response = $this->middleware->handle($request, function ($req) {
            return response('OK', 200);
        });

        $this->assertEquals(200, $response->getStatusCode());
    }

    /** @test */
    public function redirects_when_user_has_no_current_account()
    {
        $this->actingAs($this->user);
        
        $request = Request::create('/admin/dashboard', 'GET');
        $request->setUserResolver(fn() => $this->user);

        $response = $this->middleware->handle($request, function ($req) {
            return response('Should not reach here', 200);
        });

        $this->assertTrue($response->isRedirect());
    }

    /** @test */
    public function allows_account_selection_routes_without_current_account()
    {
        $this->actingAs($this->user);
        
        $request = Request::create('/admin/select-account', 'GET');
        $request->setUserResolver(fn() => $this->user);

        $response = $this->middleware->handle($request, function ($req) {
            return response('OK', 200);
        });

        $this->assertEquals(200, $response->getStatusCode());
    }

    /** @test */
    public function redirects_when_user_lost_access_to_current_account()
    {
        $this->actingAs($this->user);
        $this->user->update(['current_account_id' => $this->account->id]);

        // Remove access
        $this->user->accounts()->detach($this->account->id);

        $request = Request::create('/admin/dashboard', 'GET');
        $request->setUserResolver(fn() => $this->user);

        $response = $this->middleware->handle($request, function ($req) {
            return response('Should not reach here', 200);
        });

        $this->assertTrue($response->isRedirect());
    }
}
